package fr.sylvainjanet.tracker.importer.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sun.net.httpserver.HttpServer;
import fr.sylvainjanet.tracker.importer.source.WeightMeasurementSourceRow;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class TrackerJournalClientTest {

    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void postsOneJsonRequestPerMeasurementToTheJournalEndpoint() throws Exception {
        List<CapturedRequest> requests = new CopyOnWriteArrayList<>();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext(
                "/",
                exchange -> {
                    requests.add(
                            new CapturedRequest(
                                    exchange.getRequestURI().getPath(),
                                    exchange.getRequestMethod(),
                                    exchange.getRequestHeaders().getFirst("Content-Type"),
                                    new String(
                                            exchange.getRequestBody().readAllBytes(),
                                            StandardCharsets.UTF_8)));
                    exchange.sendResponseHeaders(200, -1);
                    exchange.close();
                });
        server.start();

        TrackerJournalClient client =
                new TrackerJournalClient(
                        URI.create("http://127.0.0.1:" + server.getAddress().getPort()),
                        Duration.ofSeconds(1));

        client.importMeasurement(
                new WeightMeasurementSourceRow(
                        2, LocalDate.of(2025, 1, 1), new BigDecimal("82.10")));
        client.importMeasurement(
                new WeightMeasurementSourceRow(
                        3, LocalDate.of(2025, 1, 2), new BigDecimal("81.90")));

        assertEquals(
                List.of(
                        new CapturedRequest(
                                "/api/journal/weight-measurement",
                                "POST",
                                "application/json",
                                """
                                {"date":"2025-01-01","weightInKg":82.10}\
                                """),
                        new CapturedRequest(
                                "/api/journal/weight-measurement",
                                "POST",
                                "application/json",
                                """
                                {"date":"2025-01-02","weightInKg":81.90}\
                                """)),
                requests);
    }

    @Test
    void rejectsNonSuccessResponsesWithSafeMeasurementDetails() throws Exception {
        byte[] responseBody = "sensitive server response".getBytes(StandardCharsets.UTF_8);

        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext(
                "/",
                exchange -> {
                    exchange.sendResponseHeaders(500, responseBody.length);
                    exchange.getResponseBody().write(responseBody);
                    exchange.close();
                });
        server.start();

        TrackerJournalClient client =
                new TrackerJournalClient(
                        URI.create("http://127.0.0.1:" + server.getAddress().getPort()),
                        Duration.ofSeconds(1));
        WeightMeasurementSourceRow measurement =
                new WeightMeasurementSourceRow(
                        2, LocalDate.of(2025, 1, 1), new BigDecimal("82.10"));

        TrackerJournalClientException exception =
                assertThrows(
                        TrackerJournalClientException.class,
                        () -> client.importMeasurement(measurement));

        assertEquals(
                "Tracker returned HTTP 500 for source row 2 dated 2025-01-01",
                exception.getMessage());
        assertFalse(exception.getMessage().contains("82.10"));
        assertFalse(exception.getMessage().contains("sensitive server response"));
    }

    @Test
    void reportsConnectionFailuresWithSafeMeasurementDetails() throws Exception {
        int unavailablePort;
        try (ServerSocket socket = new ServerSocket(0, 0, InetAddress.getByName("127.0.0.1"))) {
            unavailablePort = socket.getLocalPort();
        }

        TrackerJournalClient client =
                new TrackerJournalClient(
                        URI.create("http://127.0.0.1:" + unavailablePort), Duration.ofSeconds(1));
        WeightMeasurementSourceRow measurement =
                new WeightMeasurementSourceRow(
                        2, LocalDate.of(2025, 1, 1), new BigDecimal("82.10"));

        TrackerJournalClientException exception =
                assertThrows(
                        TrackerJournalClientException.class,
                        () -> client.importMeasurement(measurement));

        assertEquals(
                "Could not connect to Tracker for source row 2 dated 2025-01-01",
                exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());
        assertFalse(exception.getMessage().contains("82.10"));
    }

    @Test
    void reportsRequestTimeoutsWithSafeMeasurementDetails() throws Exception {
        CountDownLatch responseAllowed = new CountDownLatch(1);

        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext(
                "/",
                exchange -> {
                    try {
                        responseAllowed.await(1, TimeUnit.SECONDS);
                        exchange.sendResponseHeaders(200, -1);
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                    } finally {
                        exchange.close();
                    }
                });
        server.start();

        TrackerJournalClient client =
                new TrackerJournalClient(
                        URI.create("http://127.0.0.1:" + server.getAddress().getPort()),
                        Duration.ofMillis(50));
        WeightMeasurementSourceRow measurement =
                new WeightMeasurementSourceRow(
                        2, LocalDate.of(2025, 1, 1), new BigDecimal("82.10"));

        TrackerJournalClientException exception;
        try {
            exception =
                    assertThrows(
                            TrackerJournalClientException.class,
                            () -> client.importMeasurement(measurement));
        } finally {
            responseAllowed.countDown();
        }

        assertEquals(
                "Tracker request timed out for source row 2 dated 2025-01-01",
                exception.getMessage());
        assertInstanceOf(HttpTimeoutException.class, exception.getCause());
        assertFalse(exception.getMessage().contains("82.10"));
    }

    private record CapturedRequest(String path, String method, String contentType, String body) {}
}
