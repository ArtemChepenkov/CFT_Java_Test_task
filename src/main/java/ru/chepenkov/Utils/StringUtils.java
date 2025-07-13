package ru.chepenkov.Utils;

import java.math.BigDecimal;
import java.math.BigInteger;

public class StringUtils {


    private StringUtils() {
    }

    public static boolean isAllIntegers(String line) {
        if (line == null || line.isEmpty()) {
            return false;
        }
        try {
            BigDecimal temp = new BigDecimal(line);
            temp.toBigIntegerExact();
            return true;
        } catch (ArithmeticException | NumberFormatException e) {
            return false;
        }
    }

    public static boolean isAllFloats(String line) {
        if (line == null || line.isEmpty()) {
            return false;
        }
        try {
            BigDecimal temp = new BigDecimal(line);
            temp.toBigIntegerExact();
            return false;
        } catch (ArithmeticException e) {
            return !isAllIntegers(line);
        } catch (NumberFormatException e) {
            return false;
        }

    }
}
