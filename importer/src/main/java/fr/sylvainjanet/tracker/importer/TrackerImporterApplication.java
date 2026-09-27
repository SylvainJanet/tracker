package fr.sylvainjanet.tracker.importer;

import fr.sylvainjanet.tracker.importer.cli.ImporterArguments;
import fr.sylvainjanet.tracker.importer.cli.ImporterArgumentsParser;
import fr.sylvainjanet.tracker.importer.cli.InvalidImporterArgumentsException;
import fr.sylvainjanet.tracker.importer.source.CsvWeightMeasurementReader;
import fr.sylvainjanet.tracker.importer.source.InvalidWeightMeasurementSourceException;
import fr.sylvainjanet.tracker.importer.source.WeightMeasurementSource;
import java.io.IOException;
import java.io.PrintStream;

public final class TrackerImporterApplication {

    private static final int SUCCESS_EXIT_CODE = 0;
    private static final int INVALID_INPUT_EXIT_CODE = 2;

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

            output.printf(
                    "Source rows: %d; accepted measurements: %d; "
                            + "skipped blank weights: %d; "
                            + "skipped after cutoff: %d%n",
                    source.sourceRowCount(),
                    source.measurements().size(),
                    source.skippedBlankWeightCount(),
                    source.skippedAfterCutoffCount());

            return SUCCESS_EXIT_CODE;
        } catch (InvalidImporterArgumentsException
                | InvalidWeightMeasurementSourceException exception) {
            error.println(exception.getMessage());
            return INVALID_INPUT_EXIT_CODE;
        } catch (IOException _) {
            error.println("Could not read input CSV");
            return INVALID_INPUT_EXIT_CODE;
        }
    }
}
