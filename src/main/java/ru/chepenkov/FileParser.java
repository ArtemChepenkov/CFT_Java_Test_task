package ru.chepenkov;

import ru.chepenkov.DataTypes.TypeData;
import ru.chepenkov.Utils.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class FileParser implements Callable<Map<String, TypeData<?>>> {
    private final String filename;
    private final Map<String, TypeData<?>> allData;


    public FileParser(String filename) {
        this.filename = filename;
        this.allData = new HashMap<>();
        allData.put("integer", new TypeData<BigInteger>());

        allData.put("float", new TypeData<BigDecimal>());

        allData.put("string", new TypeData<String>());
    }

    @Override
    public Map<String, TypeData<?>> call() throws FileNotFoundException, Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;


            while ((line = br.readLine()) != null) {
                System.out.println(line);
                Main.elementsCounter.incrementAndGet();
                if (StringUtils.isAllIntegers(line)) {
                    BigInteger curInteger = new BigInteger(line);
                    BigDecimal curFloat = new BigDecimal(line);
                    Main.numberSum.updateAndGet(current -> current.add(curFloat));
                    Main.integersCounter.incrementAndGet();
                    Main.integerMax.update(curInteger);
                    Main.integerMin.update(curInteger);
                    @SuppressWarnings("unchecked")
                    TypeData<BigInteger> intData = (TypeData<BigInteger>) allData.get("integer");
                    intData.addElement(curInteger);
                } else if (StringUtils.isAllFloats(line)) {
                    BigDecimal curFloat = new BigDecimal(line);
                    Main.numberSum.updateAndGet(current -> current.add(curFloat));
                    Main.floatsCounter.incrementAndGet();
                    Main.floatMax.update(curFloat);
                    Main.floatMin.update(curFloat);
                    @SuppressWarnings("unchecked")
                    TypeData<BigDecimal> floatData = (TypeData<BigDecimal>) allData.get("float");
                    floatData.addElement(curFloat);

                } else {
                    Main.stringsCounter.incrementAndGet();
                    Main.stringMax.update(line.length());
                    Main.stringMin.update(line.length());
                    @SuppressWarnings("unchecked")
                    TypeData<String> stringData = (TypeData<String>) allData.get("string");
                    stringData.addElement(line);

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return allData;
    }
}
