package ru.chepenkov.Utils;

public class StringUtils {


    private StringUtils() {}

    public static boolean isAllIntegers(String line) {
        char[] lineChars = line.toCharArray();

        for (char c: lineChars) {
            if (!Character.isDigit(c) && c != '-') {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllFloats(String line) {
        char[] lineChars = line.toCharArray();
        int pointAmout = 0;


        for (char c: lineChars) {
            if (c == '.') {
                pointAmout++;

                if (pointAmout > 1) {
                    return false;
                }
            } else if (!Character.isDigit(c) && c != '-') {
                return false;
            }
        }
        return true;
    }

}
