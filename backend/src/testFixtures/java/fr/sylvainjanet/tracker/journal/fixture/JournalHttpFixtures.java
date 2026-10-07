package fr.sylvainjanet.tracker.journal.fixture;

import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.START_DATE;

public final class JournalHttpFixtures {

    public static final String WEIGHT_MEASUREMENT_PATH = "/api/journal/weight-measurement";
    public static final String WEIGHT_MEASUREMENT_BY_DATE_PATH =
            WEIGHT_MEASUREMENT_PATH + "/" + START_DATE;
    public static final String INVALID_DATE_PATH = WEIGHT_MEASUREMENT_PATH + "/not-a-date";

    public static final String LOG_REQUEST_JSON =
            """
            {
              "date": "2026-09-01",
              "weightInKg": 82.10
            }
            """;
    public static final String MISSING_DATE_REQUEST_JSON =
            """
            {
              "weightInKg": 82.10
            }
            """;
    public static final String MISSING_WEIGHT_REQUEST_JSON =
            """
            {
              "date": "2026-09-01"
            }
            """;
    public static final String INVALID_DATE_REQUEST_JSON =
            """
            {
              "date": "2026-02-31",
              "weightInKg": 82.10
            }
            """;
    public static final String INVALID_WEIGHT_REQUEST_JSON =
            """
            {
              "date": "2026-09-01",
              "weightInKg": -82.10
            }
            """;

    public static final String WEIGHT_MEASUREMENT_RESPONSE_JSON =
            """
            {
              "date": "2026-09-01",
              "weightInKg": 82.10
            }
            """;

    private JournalHttpFixtures() {}
}
