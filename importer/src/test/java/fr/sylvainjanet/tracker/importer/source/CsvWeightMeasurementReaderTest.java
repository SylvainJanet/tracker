package fr.sylvainjanet.tracker.importer.source;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CsvWeightMeasurementReaderTest {

    @TempDir Path temporaryDirectory;

    @Test
    void readsRequiredColumnsByHeaderAndMapsSpreadsheetValues() throws Exception {
        Path input = temporaryDirectory.resolve("weights.csv");
        Files.writeString(
                input,
                """
                Notes;Date;;Weight;Day
                "synthetic; note";03/11/25;ignored;95,1;Monday
                """,
                StandardCharsets.UTF_8);

        WeightMeasurementSource source =
                new CsvWeightMeasurementReader().read(input, LocalDate.of(2025, 11, 3));

        assertEquals(1, source.sourceRowCount());
        assertEquals(0, source.skippedBlankWeightCount());
        assertEquals(
                List.of(
                        new WeightMeasurementSourceRow(
                                2, LocalDate.of(2025, 11, 3), new BigDecimal("95.1"))),
                source.measurements());
    }

    @Test
    void skipsRecordsWhoseWeightIsBlank() throws Exception {
        Path input = temporaryDirectory.resolve("weights-with-blank.csv");
        Files.writeString(
                input,
                """
                Date;Weight;Notes
                01/11/25;95,4;first
                02/11/25;   ;blank
                03/11/25;95,1;last
                """,
                StandardCharsets.UTF_8);

        WeightMeasurementSource source =
                new CsvWeightMeasurementReader().read(input, LocalDate.of(2025, 11, 3));

        assertEquals(3, source.sourceRowCount());
        assertEquals(1, source.skippedBlankWeightCount());
        assertEquals(
                List.of(
                        new WeightMeasurementSourceRow(
                                2, LocalDate.of(2025, 11, 1), new BigDecimal("95.4")),
                        new WeightMeasurementSourceRow(
                                4, LocalDate.of(2025, 11, 3), new BigDecimal("95.1"))),
                source.measurements());
    }

    @Test
    void rejectsSourceWithoutDateHeaderBeforeParsingRows() throws Exception {
        Path input = temporaryDirectory.resolve("missing-date-header.csv");
        Files.writeString(
                input,
                """
                Weight;Notes
                not-a-weight;synthetic
                """,
                StandardCharsets.UTF_8);

        InvalidWeightMeasurementSourceException exception =
                assertThrows(
                        InvalidWeightMeasurementSourceException.class,
                        () ->
                                new CsvWeightMeasurementReader()
                                        .read(input, LocalDate.of(2025, 11, 3)));

        assertEquals("Missing required header: Date", exception.getMessage());
    }

    @Test
    void rejectsSourceWithoutWeightHeaderBeforeParsingRows() throws Exception {
        Path input = temporaryDirectory.resolve("missing-weight-header.csv");
        Files.writeString(
                input,
                """
                Date;Notes
                not-a-date;synthetic
                """,
                StandardCharsets.UTF_8);

        InvalidWeightMeasurementSourceException exception =
                assertThrows(
                        InvalidWeightMeasurementSourceException.class,
                        () ->
                                new CsvWeightMeasurementReader()
                                        .read(input, LocalDate.of(2025, 11, 3)));

        assertEquals("Missing required header: Weight", exception.getMessage());
    }

    @Test
    void rejectsMalformedDatesWithASafeRowDiagnostic() throws Exception {
        Path input = temporaryDirectory.resolve("malformed-date.csv");
        Files.writeString(
                input,
                """
                Date;Weight
                31/02/25;95,1
                """,
                StandardCharsets.UTF_8);

        InvalidWeightMeasurementSourceException exception =
                assertThrows(
                        InvalidWeightMeasurementSourceException.class,
                        () ->
                                new CsvWeightMeasurementReader()
                                        .read(input, LocalDate.of(2025, 11, 3)));

        assertEquals("Invalid date at source row 2", exception.getMessage());
    }

    @Test
    void rejectsMalformedWeightsWithoutExposingTheirValue() throws Exception {
        Path input = temporaryDirectory.resolve("malformed-weight.csv");
        Files.writeString(
                input,
                """
                Date;Weight
                03/11/25;private-invalid-value
                """,
                StandardCharsets.UTF_8);

        InvalidWeightMeasurementSourceException exception =
                assertThrows(
                        InvalidWeightMeasurementSourceException.class,
                        () ->
                                new CsvWeightMeasurementReader()
                                        .read(input, LocalDate.of(2025, 11, 3)));

        assertEquals("Invalid weight at source row 2", exception.getMessage());
    }

    @Test
    void rejectsDuplicateMeasurementDates() throws Exception {
        Path input = temporaryDirectory.resolve("duplicate-date.csv");
        Files.writeString(
                input,
                """
                Date;Weight
                01/11/25;95,4
                01/11/25;95,1
                """,
                StandardCharsets.UTF_8);

        InvalidWeightMeasurementSourceException exception =
                assertThrows(
                        InvalidWeightMeasurementSourceException.class,
                        () ->
                                new CsvWeightMeasurementReader()
                                        .read(input, LocalDate.of(2025, 11, 3)));

        assertEquals(
                "Duplicate measurement date 2025-11-01 at source row 3", exception.getMessage());
    }

    @Test
    void skipsNonblankWeightsAfterTheLastMeasuredDate() throws Exception {
        Path input = temporaryDirectory.resolve("after-cutoff.csv");
        Files.writeString(
                input,
                """
                Date;Weight
                03/11/25;95,4
                04/11/25;ignored-prediction
                """,
                StandardCharsets.UTF_8);

        WeightMeasurementSource source =
                new CsvWeightMeasurementReader().read(input, LocalDate.of(2025, 11, 3));

        assertEquals(2, source.sourceRowCount());
        assertEquals(0, source.skippedBlankWeightCount());
        assertEquals(1, source.skippedAfterCutoffCount());
        assertEquals(
                List.of(
                        new WeightMeasurementSourceRow(
                                2, LocalDate.of(2025, 11, 3), new BigDecimal("95.4"))),
                source.measurements());
    }

    @Test
    void trimsValuesAndInterpretsTwoDigitYearsAs2000Through2099() throws Exception {
        Path input = temporaryDirectory.resolve("two-digit-years.csv");
        Files.writeString(
                input,
                """
                Date;Weight
                 01/01/00 ; 95,4
                 31/12/99 ; 95,1
                """,
                StandardCharsets.UTF_8);

        WeightMeasurementSource source =
                new CsvWeightMeasurementReader().read(input, LocalDate.of(2099, 12, 31));

        assertEquals(
                List.of(
                        new WeightMeasurementSourceRow(
                                2, LocalDate.of(2000, 1, 1), new BigDecimal("95.4")),
                        new WeightMeasurementSourceRow(
                                3, LocalDate.of(2099, 12, 31), new BigDecimal("95.1"))),
                source.measurements());
    }

    @Test
    void skipsBlankWeightsAfterTheLastMeasuredDate() throws Exception {
        Path input = temporaryDirectory.resolve("blank-after-cutoff.csv");
        Files.writeString(
                input,
                """
                Date;Weight
                03/11/25;95,1
                04/11/25;
                """,
                StandardCharsets.UTF_8);

        WeightMeasurementSource source =
                new CsvWeightMeasurementReader().read(input, LocalDate.of(2025, 11, 3));

        assertEquals(2, source.sourceRowCount());
        assertEquals(1, source.skippedBlankWeightCount());
        assertEquals(
                List.of(
                        new WeightMeasurementSourceRow(
                                2, LocalDate.of(2025, 11, 3), new BigDecimal("95.1"))),
                source.measurements());
    }
}
