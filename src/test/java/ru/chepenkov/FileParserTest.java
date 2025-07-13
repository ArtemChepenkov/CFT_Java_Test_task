package ru.chepenkov;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.chepenkov.DataTypes.TypeData;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileParserTest {

    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        Main.elementsCounter.set(0);
        Main.integersCounter.set(0);
        Main.floatsCounter.set(0);
        Main.stringsCounter.set(0);
        Main.numberSum.set(BigDecimal.ZERO);
        Main.integerMin = new ru.chepenkov.DataTypes.AtomicMinTracker<>(null);
        Main.integerMax = new ru.chepenkov.DataTypes.AtomicMaxTracker<>(null);
        Main.floatMin = new ru.chepenkov.DataTypes.AtomicMinTracker<>(null);
        Main.floatMax = new ru.chepenkov.DataTypes.AtomicMaxTracker<>(null);
        Main.stringMin = new ru.chepenkov.DataTypes.AtomicMinTracker<>(null);
        Main.stringMax = new ru.chepenkov.DataTypes.AtomicMaxTracker<>(null);
    }

    @AfterEach
    void deleteFile() throws IOException {
        if (tempFile != null) {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void testParsingCorrectlyClassifiesData() throws Exception {
        tempFile = Files.createTempFile("testData", ".txt");
        Files.write(tempFile, List.of("123", "3.14", "hello world", "1e3"));

        FileParser parser = new FileParser(tempFile.toString());
        Map<String, TypeData<?>> result = parser.call();

        assertEquals(2, result.get("integer").getAmount());
        assertEquals(1, result.get("float").getAmount());
        assertEquals(1, result.get("string").getAmount());

        assertTrue(((TypeData<BigInteger>) result.get("integer")).getElements().contains(new BigInteger("123")));
        assertTrue(((TypeData<BigInteger>) result.get("integer")).getElements().contains(new BigInteger("1000")));
        assertTrue(((TypeData<BigDecimal>) result.get("float")).getElements().contains(new BigDecimal("3.14")));
        assertTrue(((TypeData<String>) result.get("string")).getElements().contains("hello world"));
    }

    @Test
    void testEmptyFileProducesEmptyResults() throws Exception {
        tempFile = Files.createTempFile("emptyTest", ".txt");

        FileParser parser = new FileParser(tempFile.toString());
        Map<String, TypeData<?>> result = parser.call();

        assertEquals(0, result.get("integer").getAmount());
        assertEquals(0, result.get("float").getAmount());
        assertEquals(0, result.get("string").getAmount());
    }

    @Test
    void testFileWithOnlyStrings() throws Exception {
        tempFile = Files.createTempFile("onlyStrings", ".txt");
        Files.write(tempFile, List.of("hello", "world", "test"));

        FileParser parser = new FileParser(tempFile.toString());
        Map<String, TypeData<?>> result = parser.call();

        assertEquals(0, result.get("integer").getAmount());
        assertEquals(0, result.get("float").getAmount());
        assertEquals(3, result.get("string").getAmount());
    }

    @Test
    void testMalformedLinesAreClassifiedAsStrings() throws Exception {
        tempFile = Files.createTempFile("mixed", ".txt");
        Files.write(tempFile, List.of("1.2.3", "--42", "text"));

        FileParser parser = new FileParser(tempFile.toString());
        Map<String, TypeData<?>> result = parser.call();

        assertEquals(0, result.get("integer").getAmount());
        assertEquals(0, result.get("float").getAmount());
        assertEquals(3, result.get("string").getAmount());
    }

}
