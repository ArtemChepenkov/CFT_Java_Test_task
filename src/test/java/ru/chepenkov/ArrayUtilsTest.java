package ru.chepenkov;

import org.junit.jupiter.api.Test;
import ru.chepenkov.Utils.ArrayUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArrayUtilsTest {

    @Test
    void testArrayUtilsWithArray() {
        String[] flags = {"-a", "-s", "-f"};
        assertEquals(1, ArrayUtils.findIndex(flags, "-s"));
        assertEquals(-1, ArrayUtils.findIndex(flags, "-x"));
    }

    @Test
    void testArrayUtilsWithList() {
        List<String> flags = Arrays.asList("-a", "-s", "-f");
        assertEquals(2, ArrayUtils.findIndex(flags, "-f"));
        assertEquals(-1, ArrayUtils.findIndex(flags, "--help"));
    }
}
