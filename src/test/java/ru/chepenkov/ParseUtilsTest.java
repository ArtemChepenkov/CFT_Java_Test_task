package ru.chepenkov;

import org.junit.jupiter.api.Test;
import ru.chepenkov.Utils.ParseUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParseUtilsTest {

    @Test
    void testParseUtilsCheckFlagValid() {
        String[] args = {"-o", "./out", "input.txt"};
        String[] flags = {"-o", "-p"};
        assertTrue(ParseUtils.checkFlag(args, flags, "-o", 0));
    }

    @Test
    void testParseUtilsCheckFlagInvalid() {
        String[] args = {"-o", "-p", "input.txt"};
        String[] flags = {"-o", "-p"};
        assertFalse(ParseUtils.checkFlag(args, flags, "-o", 0));
    }
}
