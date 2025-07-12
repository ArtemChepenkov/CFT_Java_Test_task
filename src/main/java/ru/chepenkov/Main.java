package ru.chepenkov;

import ru.chepenkov.DataTypes.TypeData;
import ru.chepenkov.Utils.ArrayUtils;
import ru.chepenkov.Utils.ParseUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    static AtomicLong elementsCounter = new AtomicLong(0L);
    static AtomicLong integersCounter = new AtomicLong(0L);
    static AtomicLong floatsCounter = new AtomicLong(0L);
    static AtomicLong stringsCounter = new AtomicLong(0L);
    static AtomicReference<BigDecimal> numberSum = new AtomicReference<>(BigDecimal.ZERO);
    static AtomicMinTracker<BigInteger> integerMin = new AtomicMinTracker<>(null);
    static AtomicMaxTracker<BigInteger> integerMax = new AtomicMaxTracker<>(null);
    static AtomicMinTracker<BigDecimal> floatMin = new AtomicMinTracker<>(null);
    static AtomicMaxTracker<BigDecimal> floatMax = new AtomicMaxTracker<>(null);
    static AtomicMinTracker<Integer> stringMin = new AtomicMinTracker<>(null);
    static AtomicMaxTracker<Integer> stringMax = new AtomicMaxTracker<>(null);

    public static void main(String[] args) throws IOException {
        String[] flags = {"-o", "-p", "-a", "-s", "-f"};
        int oIndex = ArrayUtils.findIndex(args, "-o");
        int pIndex = ArrayUtils.findIndex(args, "-p");
        int aIndex = ArrayUtils.findIndex(args, "-a");
        int sIndex = ArrayUtils.findIndex(args, "-s");
        int fIndex = ArrayUtils.findIndex(args, "-f");

        String resultPath = "./";
        String prefix = "";

        ArrayList<Integer> takenIndecies = new ArrayList<>();
        ArrayList<String> inputFiles = new ArrayList<>();


        // -o case
        if (oIndex != -1) {
            if (ParseUtils.checkFlag(args, flags, "-o", oIndex)) {
                resultPath = args[oIndex + 1];

            } else {
                System.err.println("\n-o requires file path after itself: -o <path>" +
                        "\n" + "Using ./ path\n");
            }
            takenIndecies.add(oIndex);
        }
        // -p case
        if (pIndex != -1) {
            if (ParseUtils.checkFlag(args, flags, "-p", pIndex)) {
                prefix = args[pIndex + 1];
            } else {
                System.err.println("\n-p requires file prefix after itself: -p <prefix>" +
                        "\n" + "Using empty prefix\n");
            }
            takenIndecies.add(pIndex);
        }
        // -a case
        if (aIndex != -1) {
            takenIndecies.add(aIndex);
        }
        // -s case
        if (sIndex != -1) {
            takenIndecies.add(sIndex);
        }
        // -f case
        if (fIndex != -1) {
            takenIndecies.add(fIndex);
        }

        for (int i = 0; i < args.length; i++) {
            if (ArrayUtils.findIndex(takenIndecies, i) == -1) {
                inputFiles.add(args[i]);
                System.out.println(args[i]);
            }
        }

        List<Future<Map<String, TypeData<?>>>> futures = new ArrayList<>();
        List<Map<String, TypeData<?>>> results = new ArrayList<>();
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            for (String file : inputFiles) {
                FileParser parser = new FileParser(file);
                futures.add(executorService.submit(parser));
            }


            for (Future<Map<String, TypeData<?>>> future : futures) {
                try {
                    results.add(future.get());
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e); //добавить поподробней
                }
            }
        }


        boolean hasFloat = false;
        boolean hasInteger = false;
        boolean hasString = false;

        String commonName = resultPath + prefix;
        String floatName = "";
        String integerName = "";
        String stringName = "";

        List<BigInteger> integers = new ArrayList<>();
        List<BigDecimal> floats = new ArrayList<>();
        List<String> strings = new ArrayList<>();

        for (Map<String, TypeData<?>> result : results) {
            if (result.get("string").getAmount() != 0) {
                hasString = true;
            }
            if (result.get("integer").getAmount() != 0) {
                hasInteger = true;
            }
            if (result.get("float").getAmount() != 0) {
                hasFloat = true;
            }
        }

        for (Map<String, TypeData<?>> result : results) {
            @SuppressWarnings("unchecked")
            TypeData<BigInteger> intValues = (TypeData<BigInteger>) result.get("integer");
            @SuppressWarnings("unchecked")
            TypeData<BigDecimal> floatValues = (TypeData<BigDecimal>) result.get("float");
            @SuppressWarnings("unchecked")
            TypeData<String> stringValues = (TypeData<String>) result.get("string");
            if (!intValues.isEmpty()) {
                integers.addAll(intValues.getElements());
            }
            if (!floatValues.isEmpty()) {
                floats.addAll(floatValues.getElements());
            }
            if (!stringValues.isEmpty()) {
                strings.addAll(stringValues.getElements());
            }
        }

        BigDecimal maxIntToDecimal = null;
        BigDecimal minIntToDecimal = null;

        if (integerMax != null && integerMin != null) {
            maxIntToDecimal = new BigDecimal(integerMax.getMax());
            minIntToDecimal = new BigDecimal(integerMin.getMin());
        }

        if (hasFloat) {
            writeFile(commonName, "floats.txt", floats, aIndex != -1);
        }
        if (hasInteger) {
            writeFile(commonName, "integers.txt", integers, aIndex != -1);
        }
        if (hasString) {
            writeFile(commonName, "strings.txt", strings, aIndex != -1);
        }

        if (fIndex != -1) {
            System.out.println("----------------Статистика----------------");
            System.out.println("Всего элементов: " + elementsCounter.get());
            System.out.println("Строк: " + stringsCounter.get());
            System.out.println("Целых чисел: " + integersCounter.get());
            System.out.println("Дробных чисел: " + floatsCounter.get());
            if (floatMax.getMax() != null && maxIntToDecimal != null) {
                System.out.println("Максимальное число: " + (floatMax.getMax().compareTo(maxIntToDecimal) == 1 ? floatMax.getMax() : maxIntToDecimal));
            } else if (floatMax.getMax() == null) {
                System.out.println("Максимальное число: " + maxIntToDecimal);
            } else {
                System.out.println("Максимальное число: " + floatMax.getMax());
            }
            if (floatMin.getMin() != null && maxIntToDecimal != null) {
                System.out.println("Минимальное число: " + (floatMin.getMin().compareTo(minIntToDecimal) == -1 ? floatMin.getMin() : minIntToDecimal));
            } else if (floatMin.getMin() == null) {
                System.out.println("Минимальное число: " + minIntToDecimal);
            } else {
                System.out.println("Минимальное число: " + floatMin.getMin());
            }
            System.out.println("Сумма: " + numberSum);
            System.out.println("Среднее: " + numberSum.get().divide(
                    new BigDecimal(integersCounter.get() + floatsCounter.get()))
            );
            if (stringMax.getMax() == null) {
                stringMax = new AtomicMaxTracker<>(0);
            }
            if (stringMin.getMin() == null) {
                stringMin = new AtomicMinTracker<>(0);
            }
            System.out.println("Длина самой длинной строки: " + stringMax.getMax());
            System.out.println("Длина самой короткой строки: " + stringMin.getMin());
        } else if (sIndex != -1) {
            System.out.println("Всего элементов: " + elementsCounter);
        }

    }

    public static void writeFile(String wholePrefix, String baseFileName, List<?> sourceArray, boolean needAppend) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(wholePrefix + baseFileName, needAppend))) {
            if (needAppend) {
                bufferedWriter.newLine();
            }
            for (int i = 0; i < sourceArray.size(); i++) {
                bufferedWriter.write(String.valueOf(sourceArray.get(i)));
                if (i != sourceArray.size() - 1) {
                    bufferedWriter.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
