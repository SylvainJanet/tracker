package fr.sylvainjanet.tracker.importer;

import fr.sylvainjanet.tracker.importer.api.TrackerJournalClient;
import fr.sylvainjanet.tracker.importer.api.TrackerJournalClientException;
import fr.sylvainjanet.tracker.importer.cli.ImporterArguments;
import fr.sylvainjanet.tracker.importer.cli.ImporterArgumentsParser;
import fr.sylvainjanet.tracker.importer.cli.InvalidImporterArgumentsException;
import fr.sylvainjanet.tracker.importer.source.CsvWeightMeasurementReader;
import fr.sylvainjanet.tracker.importer.source.InvalidWeightMeasurementSourceException;
import fr.sylvainjanet.tracker.importer.source.WeightMeasurementSource;
import fr.sylvainjanet.tracker.importer.source.WeightMeasurementSourceRow;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Duration;

public final class TrackerImporterApplication {

    private static final int SUCCESS_EXIT_CODE = 0;
    private static final int INVALID_INPUT_EXIT_CODE = 2;
    private static final int IMPORT_FAILURE_EXIT_CODE = 3;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    public static void main(String[] arguments) {
        int exitCode = new TrackerImporterApplication().run(arguments, System.out, System.err);

        if (exitCode != SUCCESS_EXIT_CODE) {
            System.exit(exitCode);
        }
    }

    int run(String[] arguments, PrintStream output, PrintStream error) {
        try {
            ImporterArguments importerArguments = new ImporterArgumentsParser().parse(arguments);

            WeightMeasurementSource source =
                    new CsvWeightMeasurementReader()
                            .read(importerArguments.input(), importerArguments.through());

            TrackerJournalClient client =
                    new TrackerJournalClient(importerArguments.baseUrl(), REQUEST_TIMEOUT);

            int successfulImportCount = 0;
            try {
                for (WeightMeasurementSourceRow measurement : source.measurements()) {
                    client.importMeasurement(measurement);
                    successfulImportCount++;
                }
            } catch (TrackerJournalClientException exception) {
                printSummary(output, source, successfulImportCount, 1);
                error.println(exception.getMessage());
                return IMPORT_FAILURE_EXIT_CODE;
            }

            printSummary(output, source, successfulImportCount, 0);
            return SUCCESS_EXIT_CODE;
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
            error.println("Import interrupted");
            return IMPORT_FAILURE_EXIT_CODE;
        } catch (InvalidImporterArgumentsException
                | InvalidWeightMeasurementSourceException exception) {
            error.println(exception.getMessage());
            return INVALID_INPUT_EXIT_CODE;
        } catch (IOException _) {
            error.println("Could not read input CSV");
            return INVALID_INPUT_EXIT_CODE;
        }
    }

    private static void printSummary(
            PrintStream output,
            WeightMeasurementSource source,
            int successfulImportCount,
            int failureCount) {
        output.printf(
                "Source rows: %d; accepted measurements: %d; "
                        + "skipped blank weights: %d; "
                        + "skipped after cutoff: %d; "
                        + "successful imports: %d; failures: %d%n",
                source.sourceRowCount(),
                source.measurements().size(),
                source.skippedBlankWeightCount(),
                source.skippedAfterCutoffCount(),
                successfulImportCount,
                failureCount);
    }
}
