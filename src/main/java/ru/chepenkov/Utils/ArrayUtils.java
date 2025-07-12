package ru.chepenkov.Utils;

import java.util.List;
import java.util.Objects;

public class ArrayUtils {


    private ArrayUtils() {}

    public static <T> int findIndex(T[] array, T target) {
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], target)) {
                return i;
            }
        }
        return -1;
    }

    public static <T> int findIndex(List<T> array, T target) {
        int len = array.toArray().length;
        for (int i = 0; i < len; i++) {
            if (Objects.equals(array.get(i), target)) {
                return i;
            }
        }
        return -1;
    }
}
