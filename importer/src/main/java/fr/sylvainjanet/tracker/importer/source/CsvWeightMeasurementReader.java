package fr.sylvainjanet.tracker.importer.source;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public final class CsvWeightMeasurementReader {

    private static final String DATE_HEADER = "Date";
    private static final String WEIGHT_HEADER = "Weight";

    private static final CSVFormat SOURCE_FORMAT =
            CSVFormat.DEFAULT
                    .builder()
                    .setDelimiter(';')
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setAllowMissingColumnNames(true)
                    .setTrim(true)
                    .get();

    private static final DateTimeFormatter SOURCE_DATE_FORMAT =
            new DateTimeFormatterBuilder()
                    .parseStrict()
                    .appendPattern("dd/MM/")
                    .appendValueReduced(ChronoField.YEAR, 2, 2, 2000)
                    .toFormatter(Locale.ROOT)
                    .withResolverStyle(ResolverStyle.STRICT);

    public WeightMeasurementSource read(Path input, LocalDate lastMeasuredDate) throws IOException {
        List<WeightMeasurementSourceRow> measurements = new ArrayList<>();
        Set<LocalDate> measurementDates = new HashSet<>();
        int sourceRowCount = 0;
        int skippedBlankWeightCount = 0;
        int skippedAfterCutoffCount = 0;

        try (Reader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8);
                CSVParser parser = SOURCE_FORMAT.parse(reader)) {
            requireHeader(parser, DATE_HEADER);
            requireHeader(parser, WEIGHT_HEADER);

            for (CSVRecord csvRecord : parser) {
                sourceRowCount++;

                String weightValue = csvRecord.get(WEIGHT_HEADER);
                if (weightValue.isBlank()) {
                    skippedBlankWeightCount++;
                    continue;
                }

                LocalDate date = parseDate(csvRecord);
                if (date.isAfter(lastMeasuredDate)) {
                    skippedAfterCutoffCount++;
                    continue;
                }

                BigDecimal weight = parseWeight(csvRecord, weightValue);
                requireUniqueDate(csvRecord, date, measurementDates);

                measurements.add(
                        new WeightMeasurementSourceRow(sourceRowNumber(csvRecord), date, weight));
            }
        }

        return new WeightMeasurementSource(
                List.copyOf(measurements),
                sourceRowCount,
                skippedBlankWeightCount,
                skippedAfterCutoffCount);
    }

    private static void requireHeader(CSVParser parser, String requiredHeader) {
        if (!parser.getHeaderMap().containsKey(requiredHeader)) {
            throw new InvalidWeightMeasurementSourceException(
                    "Missing required header: " + requiredHeader);
        }
    }

    private static LocalDate parseDate(CSVRecord csvRecord) {
        try {
            return LocalDate.parse(csvRecord.get(DATE_HEADER), SOURCE_DATE_FORMAT);
        } catch (DateTimeParseException _) {
            throw new InvalidWeightMeasurementSourceException(
                    "Invalid date at source row " + sourceRowNumber(csvRecord));
        }
    }

    private static BigDecimal parseWeight(CSVRecord csvRecord, String weightValue) {
        try {
            return new BigDecimal(weightValue.replace(',', '.'));
        } catch (NumberFormatException _) {
            throw new InvalidWeightMeasurementSourceException(
                    "Invalid weight at source row " + sourceRowNumber(csvRecord));
        }
    }

    private static long sourceRowNumber(CSVRecord csvRecord) {
        return csvRecord.getRecordNumber() + 1;
    }

    private static void requireUniqueDate(
            CSVRecord csvRecord, LocalDate date, Set<LocalDate> measurementDates) {
        if (!measurementDates.add(date)) {
            throw new InvalidWeightMeasurementSourceException(
                    "Duplicate measurement date "
                            + date
                            + " at source row "
                            + sourceRowNumber(csvRecord));
        }
    }
}
