package ru.chepenkov;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import ru.chepenkov.Utils.StringUtils;

import java.math.BigDecimal;

public class StringUtilsTest {

    @Test
    void testIsAllIntegers() {
        assertTrue(StringUtils.isAllIntegers("123"));
        assertTrue(StringUtils.isAllIntegers("-456789"));
        assertTrue(StringUtils.isAllIntegers("1e5"));
        assertFalse(StringUtils.isAllIntegers("3.14"));
        assertTrue(StringUtils.isAllIntegers("1.23e5"));
        assertFalse(StringUtils.isAllIntegers("abc"));
        assertFalse(StringUtils.isAllIntegers(null));
        assertFalse(StringUtils.isAllIntegers(""));
    }

    @Test
    void testIsAllFloats() {
        assertTrue(StringUtils.isAllFloats("3.14"));
        assertTrue(StringUtils.isAllFloats("-0.0001"));
        assertFalse(StringUtils.isAllFloats("42"));
        assertFalse(StringUtils.isAllFloats("text"));
        assertFalse(StringUtils.isAllFloats(null));
        assertFalse(StringUtils.isAllFloats(""));
    }

}
