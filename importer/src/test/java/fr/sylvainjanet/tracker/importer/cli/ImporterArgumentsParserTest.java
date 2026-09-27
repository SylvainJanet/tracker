package fr.sylvainjanet.tracker.importer.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ImporterArgumentsParserTest {

    @Test
    void parsesExplicitImporterArguments() {
        ImporterArguments arguments =
                new ImporterArgumentsParser()
                        .parse(
                                new String[] {
                                    "--input",
                                    "data/synthetic-weights.csv",
                                    "--base-url",
                                    "http://127.0.0.1:8080",
                                    "--through",
                                    "2025-11-03"
                                });

        assertEquals(Path.of("data/synthetic-weights.csv"), arguments.input());
        assertEquals(URI.create("http://127.0.0.1:8080"), arguments.baseUrl());
        assertEquals(LocalDate.of(2025, 11, 3), arguments.through());
    }

    @Test
    void parsesArgumentsByNameRegardlessOfOrder() {
        ImporterArguments arguments =
                new ImporterArgumentsParser()
                        .parse(
                                new String[] {
                                    "--through",
                                    "2025-11-03",
                                    "--input",
                                    "data/synthetic-weights.csv",
                                    "--base-url",
                                    "http://127.0.0.1:8080"
                                });

        assertEquals(Path.of("data/synthetic-weights.csv"), arguments.input());
        assertEquals(URI.create("http://127.0.0.1:8080"), arguments.baseUrl());
        assertEquals(LocalDate.of(2025, 11, 3), arguments.through());
    }

    @ParameterizedTest
    @MethodSource("invalidArgumentLists")
    void rejectsInvalidArgumentLists(String[] arguments, String expectedMessage) {
        InvalidImporterArgumentsException exception =
                assertThrows(
                        InvalidImporterArgumentsException.class,
                        () -> new ImporterArgumentsParser().parse(arguments));

        assertEquals(expectedMessage, exception.getMessage());
    }

    private static Stream<Arguments> invalidArgumentLists() {
        return Stream.of(
                arguments(
                        new String[] {
                            "--input", "weights.csv",
                            "--through", "2025-11-03"
                        },
                        "Missing required argument: --base-url"),
                arguments(
                        new String[] {
                            "--input", "weights.csv",
                            "--base-url", "http://127.0.0.1:8080",
                            "--through", "2025-11-03",
                            "--unexpected", "value"
                        },
                        "Unknown argument: --unexpected"),
                arguments(
                        new String[] {
                            "--input", "weights.csv",
                            "--base-url", "http://127.0.0.1:8080",
                            "--through", "2025-11-03",
                            "--input", "other.csv"
                        },
                        "Duplicate argument: --input"),
                arguments(
                        new String[] {
                            "--input", "weights.csv",
                            "--base-url", "http://127.0.0.1:8080",
                            "--through"
                        },
                        "Missing value for argument: --through"),
                arguments(
                        new String[] {
                            "--input", "weights.csv",
                            "--base-url", "http://127.0.0.1:8080",
                            "--through", "03/11/25"
                        },
                        "Invalid value for --through: expected YYYY-MM-DD"),
                arguments(
                        new String[] {
                            "--input", "weights.csv",
                            "--base-url", "ftp://127.0.0.1:8080",
                            "--through", "2025-11-03"
                        },
                        "Invalid value for --base-url: expected an absolute HTTP(S) URL"),
                arguments(
                        new String[] {
                            "--input", "weights.csv",
                            "--base-url", "://invalid",
                            "--through", "2025-11-03"
                        },
                        "Invalid value for --base-url: expected an absolute HTTP(S) URL"),
                arguments(
                        new String[] {
                            "--input", "",
                            "--base-url", "http://127.0.0.1:8080",
                            "--through", "2025-11-03"
                        },
                        "Invalid value for --input: path must not be blank"));
    }
}
