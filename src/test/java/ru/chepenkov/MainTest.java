package ru.chepenkov;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainTest {


    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("main-test-");
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.walk(tempDir)
                .map(Path::toFile)
                .forEach(java.io.File::delete);
    }

    @Test
    void testMainWithManyLargeFiles() throws Exception {
        List<String> filePaths = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Path file = Files.createTempFile(tempDir, "input" + i, ".txt");
            List<String> lines = new ArrayList<>();
            for (int j = 0; j < 10_000; j++) {
                lines.add(String.valueOf(j));
                lines.add("3.14");
                lines.add("text" + j);
            }
            Files.write(file, lines);
            filePaths.add(file.toString());
        }

        String outputDir = tempDir.toAbsolutePath().toString() + "/out/";
        Files.createDirectories(Path.of(outputDir));

        List<String> argsList = new ArrayList<>();
        argsList.add("-o");
        argsList.add(outputDir);
        argsList.add("-p");
        argsList.add("massive_");
        argsList.addAll(filePaths);

        String[] args = argsList.toArray(new String[0]);

        long start = System.currentTimeMillis();
        Main.main(args);
        long duration = System.currentTimeMillis() - start;

        System.err.println("Multithreaded execution time: " + duration + " ms");

        Path integersFile = Path.of(outputDir + "massive_integers.txt");
        Path floatsFile = Path.of(outputDir + "massive_floats.txt");
        Path stringsFile = Path.of(outputDir + "massive_strings.txt");

        assertTrue(Files.exists(integersFile));
        assertTrue(Files.exists(floatsFile));
        assertTrue(Files.exists(stringsFile));

        assertTrue(Files.size(integersFile) > 0);
        assertTrue(Files.size(floatsFile) > 0);
        assertTrue(Files.size(stringsFile) > 0);
    }

    @Test
    void testSequentialExecutionTime() throws Exception {
        List<String> filePaths = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Path file = Files.createTempFile(tempDir, "input_seq" + i, ".txt");
            List<String> lines = new ArrayList<>();
            for (int j = 0; j < 10_000; j++) {
                lines.add(String.valueOf(j));
                lines.add("3.14");
                lines.add("text" + j);
            }
            Files.write(file, lines);
            filePaths.add(file.toString());
        }

        List<Long> durations = new ArrayList<>();

        for (String path : filePaths) {
            String[] args = {"-o", tempDir.toString() + "/", "-p", "seq_", path};
            long start = System.currentTimeMillis();
            Main.main(args);
            long duration = System.currentTimeMillis() - start;
            durations.add(duration);
        }

        long total = durations.stream().mapToLong(Long::longValue).sum();
        System.err.println("Sequential execution total time: " + total + " ms");
    }
}



