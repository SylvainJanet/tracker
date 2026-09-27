package fr.sylvainjanet.tracker.importer.api;

import fr.sylvainjanet.tracker.importer.source.WeightMeasurementSourceRow;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public final class TrackerJournalClient {

    private static final String WEIGHT_MEASUREMENT_PATH = "/api/journal/weight-measurement";

    private final HttpClient httpClient;
    private final URI endpoint;
    private final Duration requestTimeout;

    public TrackerJournalClient(URI baseUrl, Duration requestTimeout) {
        this.httpClient = HttpClient.newBuilder().connectTimeout(requestTimeout).build();
        this.endpoint = baseUrl.resolve(WEIGHT_MEASUREMENT_PATH);
        this.requestTimeout = requestTimeout;
    }

    public void importMeasurement(WeightMeasurementSourceRow measurement)
            throws InterruptedException {
        HttpRequest request =
                HttpRequest.newBuilder(endpoint)
                        .timeout(requestTimeout)
                        .header("Content-Type", "application/json")
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        requestBody(measurement), StandardCharsets.UTF_8))
                        .build();

        HttpResponse<Void> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (HttpTimeoutException exception) {
            throw new TrackerJournalClientException(
                    "Tracker request timed out for source row %d dated %s"
                            .formatted(measurement.sourceRowNumber(), measurement.date()),
                    exception);
        } catch (IOException exception) {
            throw new TrackerJournalClientException(
                    "Could not connect to Tracker for source row %d dated %s"
                            .formatted(measurement.sourceRowNumber(), measurement.date()),
                    exception);
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new TrackerJournalClientException(
                    "Tracker returned HTTP %d for source row %d dated %s"
                            .formatted(
                                    response.statusCode(),
                                    measurement.sourceRowNumber(),
                                    measurement.date()));
        }
    }

    private static String requestBody(WeightMeasurementSourceRow measurement) {
        return """
               {"date":"%s","weightInKg":%s}\
               """
                .formatted(measurement.date(), measurement.weightInKg().toPlainString());
    }
}
