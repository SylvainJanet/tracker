package fr.sylvainjanet.tracker.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TrackerImporterApplicationTest {

    @TempDir Path temporaryDirectory;

    @Test
    void importsValidatedMeasurementsAndPrintsASafeSummary() throws Exception {
        Path input = temporaryDirectory.resolve("weights.csv");
        Files.writeString(
                input,
                """
                Date;Weight
                02/11/25;95,1
                03/11/25;
                04/11/25;ignored-prediction
                """,
                StandardCharsets.UTF_8);

        AtomicInteger requestCount = new AtomicInteger();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext(
                "/api/journal/weight-measurement",
                exchange -> {
                    requestCount.incrementAndGet();
                    exchange.sendResponseHeaders(200, -1);
                    exchange.close();
                });
        server.start();

        RunResult result;
        try {
            result =
                    run(
                            "--input",
                            input.toString(),
                            "--base-url",
                            "http://127.0.0.1:" + server.getAddress().getPort(),
                            "--through",
                            "2025-11-03");
        } finally {
            server.stop(0);
        }

        assertEquals(1, requestCount.get());
        assertEquals(0, result.exitCode());
        assertEquals(
                "Source rows: 3; accepted measurements: 1; "
                        + "skipped blank weights: 1; "
                        + "skipped after cutoff: 1; "
                        + "successful imports: 1; failures: 0"
                        + System.lineSeparator(),
                result.standardOutput());
        assertEquals("", result.standardError());
        assertFalse(result.standardOutput().contains("95.1"));
        assertFalse(result.standardOutput().contains("ignored-prediction"));
    }

    @Test
    void stopsAfterTheFirstApiFailureAndPrintsAPartialSummary() throws Exception {
        Path input = temporaryDirectory.resolve("weights-with-api-failure.csv");
        Files.writeString(
                input,
                """
                Date;Weight
                01/11/25;82,10
                02/11/25;81,90
                03/11/25;80,80
                """,
                StandardCharsets.UTF_8);

        AtomicInteger requestCount = new AtomicInteger();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext(
                "/api/journal/weight-measurement",
                exchange -> {
                    int currentRequest = requestCount.incrementAndGet();
                    int status = currentRequest == 2 ? 500 : 200;
                    exchange.sendResponseHeaders(status, -1);
                    exchange.close();
                });
        server.start();

        RunResult result;
        try {
            result =
                    run(
                            "--input",
                            input.toString(),
                            "--base-url",
                            "http://127.0.0.1:" + server.getAddress().getPort(),
                            "--through",
                            "2025-11-03");
        } finally {
            server.stop(0);
        }

        assertEquals(2, requestCount.get());
        assertEquals(3, result.exitCode());
        assertEquals(
                "Source rows: 3; accepted measurements: 3; "
                        + "skipped blank weights: 0; "
                        + "skipped after cutoff: 0; "
                        + "successful imports: 1; failures: 1"
                        + System.lineSeparator(),
                result.standardOutput());
        assertEquals(
                "Tracker returned HTTP 500 for source row 3 dated 2025-11-02"
                        + System.lineSeparator(),
                result.standardError());
        assertFalse(result.standardOutput().contains("82.10"));
        assertFalse(result.standardOutput().contains("81.90"));
        assertFalse(result.standardError().contains("81.90"));
    }

    @Test
    void rejectsInvalidArgumentsBeforeReadingASource() {
        RunResult result = run("--input", "unused.csv");

        assertEquals(2, result.exitCode());
        assertEquals("", result.standardOutput());
        assertEquals(
                "Missing required argument: --base-url" + System.lineSeparator(),
                result.standardError());
    }

    @Test
    void rejectsInvalidSourceDataWithoutExposingWeightValues() throws Exception {
        Path input = temporaryDirectory.resolve("invalid-weight.csv");
        Files.writeString(
                input,
                """
                Date;Weight
                02/11/25;95,1
                03/11/25;sensitive-invalid-weight
                """,
                StandardCharsets.UTF_8);

        RunResult result =
                run(
                        "--input",
                        input.toString(),
                        "--base-url",
                        "http://127.0.0.1:8080",
                        "--through",
                        "2025-11-03");

        assertEquals(2, result.exitCode());
        assertEquals("", result.standardOutput());
        assertEquals(
                "Invalid weight at source row 3" + System.lineSeparator(), result.standardError());
        assertFalse(result.standardError().contains("sensitive-invalid-weight"));
    }

    private static RunResult run(String... arguments) {
        ByteArrayOutputStream standardOutput = new ByteArrayOutputStream();
        ByteArrayOutputStream standardError = new ByteArrayOutputStream();

        int exitCode;
        try (PrintStream output = new PrintStream(standardOutput, true, StandardCharsets.UTF_8);
                PrintStream error = new PrintStream(standardError, true, StandardCharsets.UTF_8)) {
            exitCode = new TrackerImporterApplication().run(arguments, output, error);
        }

        return new RunResult(
                exitCode,
                standardOutput.toString(StandardCharsets.UTF_8),
                standardError.toString(StandardCharsets.UTF_8));
    }

    private record RunResult(int exitCode, String standardOutput, String standardError) {}
}
