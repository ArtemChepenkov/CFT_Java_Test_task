package ru.chepenkov;

import ru.chepenkov.DataTypes.AtomicMaxTracker;
import ru.chepenkov.DataTypes.AtomicMinTracker;
import ru.chepenkov.DataTypes.TypeData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    static AtomicLong elementsCounter = new AtomicLong(0);
    static AtomicLong integersCounter = new AtomicLong(0);
    static AtomicLong floatsCounter = new AtomicLong(0);
    static AtomicLong stringsCounter = new AtomicLong(0);
    static AtomicReference<BigDecimal> numberSum = new AtomicReference<>(BigDecimal.ZERO);
    static AtomicMinTracker<BigInteger> integerMin = new AtomicMinTracker<>(null);
    static AtomicMaxTracker<BigInteger> integerMax = new AtomicMaxTracker<>(null);
    static AtomicMinTracker<BigDecimal> floatMin = new AtomicMinTracker<>(null);
    static AtomicMaxTracker<BigDecimal> floatMax = new AtomicMaxTracker<>(null);
    static AtomicMinTracker<Integer> stringMin = new AtomicMinTracker<>(null);
    static AtomicMaxTracker<Integer> stringMax = new AtomicMaxTracker<>(null);

    public static void main(String[] args) throws IOException {
        Map<String, String> options = parseArgs(args);
        List<String> inputFiles = getInputFiles(args, options);

        List<Map<String, TypeData<?>>> results = processFiles(inputFiles);
        Map<String, List<?>> categorizedData = mergeResults(results);

        writeResults(options, categorizedData);
        printStats(options);
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> opts = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-o") && i + 1 < args.length) {
                opts.put("path", args[++i]);
            } else if (args[i].equals("-p") && i + 1 < args.length) {
                opts.put("prefix", args[++i]);
            } else if (args[i].startsWith("-")) {
                opts.put(args[i], "true");
            }
        }
        opts.putIfAbsent("path", "./");
        opts.putIfAbsent("prefix", "");
        return opts;
    }

    private static List<String> getInputFiles(String[] args, Map<String, String> opts) {
        Set<Integer> takenIndexes = new HashSet<>();
        List<String> inputFiles = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if ((args[i].equals("-o") || args[i].equals("-p")) && i + 1 < args.length) {
                takenIndexes.add(i);
                takenIndexes.add(i + 1);
                i++;
            } else if (args[i].startsWith("-")) {
                takenIndexes.add(i);
            }
        }

        for (int i = 0; i < args.length; i++) {
            if (!takenIndexes.contains(i)) {
                inputFiles.add(args[i]);
            }
        }
        return inputFiles;
    }

    private static List<Map<String, TypeData<?>>> processFiles(List<String> inputFiles) {
        List<Map<String, TypeData<?>>> results = new ArrayList<>();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Map<String, TypeData<?>>>> futures = new ArrayList<>();
            for (String file : inputFiles) {
                futures.add(executor.submit(new FileParser(file)));
            }
            for (Future<Map<String, TypeData<?>>> future : futures) {
                try {
                    results.add(future.get());
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException("Ошибка обработки файла", e);
                }
            }
        }
        return results;
    }
    @SuppressWarnings("unchecked")
    private static Map<String, List<?>> mergeResults(List<Map<String, TypeData<?>>> results) {
        Map<String, List<?>> dataMap = new HashMap<>();
        dataMap.put("integer", new ArrayList<>());
        dataMap.put("float", new ArrayList<>());
        dataMap.put("string", new ArrayList<>());

        for (Map<String, TypeData<?>> result : results) {

            TypeData<BigInteger> intData = (TypeData<BigInteger>) result.get("integer");
            TypeData<BigDecimal> floatData = (TypeData<BigDecimal>) result.get("float");
            TypeData<String> stringData = (TypeData<String>) result.get("string");

            ((List<BigInteger>) dataMap.get("integer")).addAll(intData.getElements());
            ((List<BigDecimal>) dataMap.get("float")).addAll(floatData.getElements());
            ((List<String>) dataMap.get("string")).addAll(stringData.getElements());
        }
        return dataMap;
    }

    private static void writeResults(Map<String, String> opts, Map<String, List<?>> dataMap) {
        String basePath = opts.get("path") + opts.get("prefix");
        boolean append = opts.containsKey("-a");

        if (!((List<?>) dataMap.get("integer")).isEmpty()) {
            writeFile(basePath, "integers.txt", dataMap.get("integer"), append);
        }
        if (!((List<?>) dataMap.get("float")).isEmpty()) {
            writeFile(basePath, "floats.txt", dataMap.get("float"), append);
        }
        if (!((List<?>) dataMap.get("string")).isEmpty()) {
            writeFile(basePath, "strings.txt", dataMap.get("string"), append);
        }
    }

    private static void printStats(Map<String, String> opts) {
        if (opts.containsKey("-f")) {
            System.out.println("----- Полная статистика -----");
            System.out.println("Всего элементов: " + elementsCounter.get());
            System.out.println("Строк: " + stringsCounter.get());
            System.out.println("Целых чисел: " + integersCounter.get());
            System.out.println("Дробных чисел: " + floatsCounter.get());

            BigDecimal max = maxBigDecimal();
            BigDecimal min = minBigDecimal();

            System.out.println("Максимальное число: " + max);
            System.out.println("Минимальное число: " + min);
            System.out.println("Сумма: " + numberSum.get());

            long count = integersCounter.get() + floatsCounter.get();
            System.out.println("Среднее: " + (count > 0 ? numberSum.get().divide(new BigDecimal(count), RoundingMode.HALF_UP) : "0"));

            System.out.println("Длина самой длинной строки: " + safeGet(stringMax.getMax()));
            System.out.println("Длина самой короткой строки: " + safeGet(stringMin.getMin()));

        } else if (opts.containsKey("-s")) {
            System.out.println("Всего элементов: " + elementsCounter.get());
        }
    }

    private static BigDecimal maxBigDecimal() {
        if (floatMax.getMax() != null && integerMax.getMax() != null) {
            BigDecimal floatMaxVal = floatMax.getMax();
            BigDecimal intMaxVal = new BigDecimal(integerMax.getMax());
            return floatMaxVal.compareTo(intMaxVal) > 0 ? floatMaxVal : intMaxVal;
        } else if (floatMax.getMax() != null) {
            return floatMax.getMax();
        } else if (integerMax.getMax() != null) {
            return new BigDecimal(integerMax.getMax());
        } else {
            return null;
        }
    }

    private static BigDecimal minBigDecimal() {
        if (floatMin.getMin() != null && integerMin.getMin() != null) {
            BigDecimal floatMinVal = floatMin.getMin();
            BigDecimal intMinVal = new BigDecimal(integerMin.getMin());
            return floatMinVal.compareTo(intMinVal) < 0 ? floatMinVal : intMinVal;
        } else if (floatMin.getMin() != null) {
            return floatMin.getMin();
        } else if (integerMin.getMin() != null) {
            return new BigDecimal(integerMin.getMin());
        } else {
            return null;
        }
    }

    private static Object safeGet(Object value) {
        return value != null ? value : "нет данных";
    }

    public static void writeFile(String path, String filename, List<?> data, boolean append) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path + filename, append))) {
            if (append) writer.newLine();
            for (int i = 0; i < data.size(); i++) {
                writer.write(data.get(i).toString());
                if (i != data.size() - 1) writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при записи файла: " + filename, e);
        }
    }
}
