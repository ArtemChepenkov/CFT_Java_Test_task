package ru.chepenkov;

import ru.chepenkov.DataTypes.TypeData;
import ru.chepenkov.Utils.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class FileParser implements Callable<Map<String, TypeData<?>>> {

    private final String filename;
    private final Map<String, TypeData<?>> allData = new HashMap<>();

    private static final String TYPE_INTEGER = "integer";
    private static final String TYPE_FLOAT = "float";
    private static final String TYPE_STRING = "string";

    public FileParser(String filename) {
        this.filename = filename;
        allData.put(TYPE_INTEGER, new TypeData<BigInteger>());
        allData.put(TYPE_FLOAT, new TypeData<BigDecimal>());
        allData.put(TYPE_STRING, new TypeData<String>());
    }

    @Override
    public Map<String, TypeData<?>> call() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = reader.readLine()) != null) {
                Main.elementsCounter.incrementAndGet();
                line = line.trim();
                if (line.isEmpty()) continue;

                if (StringUtils.isAllIntegers(line)) {
                    processInteger(line);
                } else if (StringUtils.isAllFloats(line)) {
                    processFloat(line);
                } else {
                    processString(line);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла: " + filename, e);
        }

        return allData;
    }

    private void processInteger(String line) {
        BigDecimal temp = new BigDecimal(line);
        BigInteger value = temp.toBigIntegerExact();
        BigDecimal decimalValue = new BigDecimal(value);

        Main.integersCounter.incrementAndGet();
        Main.numberSum.updateAndGet(current -> current.add(decimalValue));
        Main.integerMax.update(value);
        Main.integerMin.update(value);

        @SuppressWarnings("unchecked")
        TypeData<BigInteger> data = (TypeData<BigInteger>) allData.get(TYPE_INTEGER);
        data.addElement(value);
    }

    private void processFloat(String line) {
        BigDecimal value = new BigDecimal(line);

        Main.floatsCounter.incrementAndGet();
        Main.numberSum.updateAndGet(current -> current.add(value));
        Main.floatMax.update(value);
        Main.floatMin.update(value);

        @SuppressWarnings("unchecked")
        TypeData<BigDecimal> data = (TypeData<BigDecimal>) allData.get(TYPE_FLOAT);
        data.addElement(value);
    }

    private void processString(String line) {
        int length = line.length();

        Main.stringsCounter.incrementAndGet();
        Main.stringMax.update(length);
        Main.stringMin.update(length);

        @SuppressWarnings("unchecked")
        TypeData<String> data = (TypeData<String>) allData.get(TYPE_STRING);
        data.addElement(line);
    }
}
