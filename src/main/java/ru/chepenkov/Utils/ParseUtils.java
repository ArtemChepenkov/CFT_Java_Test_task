package ru.chepenkov.Utils;

import ru.chepenkov.DataTypes.TypeData;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ParseUtils {

    private ParseUtils() {}

    public static boolean checkFlag(String[] args, String[] flags, String flag, int flagIndex) {
        return flagIndex + 1 < args.length
                    && ArrayUtils.findIndex(flags, args[flagIndex + 1]) == -1;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> collectStatistics(List<Map<String, TypeData<?>>> allData, boolean needWholeStatistics) {

//        BigInteger firstNotEmptyInteger = null;
//        BigDecimal firstNotEmptyFloat = null;
//        boolean foundInteger = false;
//        boolean foundFloat = false;
//
//        for (Map<String, TypeData<?>> elem: allData) {
//            if (elem.get("integer").isEmpty() && ! foundInteger) {
//                TypeData<BigInteger> integerData = (TypeData<BigInteger>)elem.get("integer");
//                firstNotEmptyInteger = integerData.getElements().getFirst();
//                foundInteger = true;
//            }
//            if (elem.get("float").isEmpty() && ! foundFloat) {
//                TypeData<BigDecimal> floatData = (TypeData<BigDecimal>)elem.get("float");
//                firstNotEmptyFloat = floatData.getElements().getFirst();
//                foundFloat = true;
//            }
//        }

//        BigInteger finalFirstNotEmptyInteger = firstNotEmptyInteger;
//        BigDecimal finalFirstNotEmptyFloat = firstNotEmptyFloat;
//        boolean finalFoundInteger = foundInteger;
//        boolean finalFoundFloat = foundFloat;
//        Map<String, Object> wholeStatistics = new HashMap<>() {{
//            put("integers", 0);
//            put("floats", 0);
//            put("strings", 0);
//            put("minInteger", finalFirstNotEmptyInteger);
//            put("maxInteger", finalFirstNotEmptyInteger);
//            put("minFloat", finalFirstNotEmptyFloat);
//            put("maxFloat", finalFirstNotEmptyFloat);
//            put("minNumber", finalFoundInteger ?finalFirstNotEmptyInteger
//                             : finalFoundFloat ?finalFirstNotEmptyFloat
//                             : null);
//            put("maxNumber", finalFoundInteger ?finalFirstNotEmptyInteger
//                             : finalFoundFloat ?finalFirstNotEmptyFloat
//                             : null);
//            put("integerAverage", null);
//            put("FloatAverage", null);
//            put("NumberAverage", null);
//        }};

//        for (Map<String, TypeData<?>> elem : allData) {
//            wholeStatistics.put("integers", (Integer)wholeStatistics.get("integers")
//                    + elem.get("integer").getAmount());
//
//            wholeStatistics.put("floats", (Integer)wholeStatistics.get("floats")
//                    + elem.get("float").getAmount());
//
//            wholeStatistics.put("strings", (Integer)wholeStatistics.get("strings")
//                    + elem.get("string").getAmount());
//            if (foundInteger && !((TypeData<BigInteger>)elem.get("integer")).isEmpty()) {
//                wholeStatistics.put("maxInteger", )
//            }
//
//        }
        return new HashMap<>();
    }
}
