package fr.sylvainjanet.tracker.importer.cli;

import java.net.URI;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ImporterArgumentsParser {

    private static final String INPUT_ARGUMENT = "--input";
    private static final String BASE_URL_ARGUMENT = "--base-url";
    private static final String THROUGH_ARGUMENT = "--through";

    private static final Set<String> SUPPORTED_ARGUMENTS =
            Set.of(INPUT_ARGUMENT, BASE_URL_ARGUMENT, THROUGH_ARGUMENT);

    public ImporterArguments parse(String[] arguments) {
        Map<String, String> values = parseNamedValues(arguments);

        String inputValue = requireArgument(values, INPUT_ARGUMENT);
        String baseUrlValue = requireArgument(values, BASE_URL_ARGUMENT);
        String throughValue = requireArgument(values, THROUGH_ARGUMENT);

        return new ImporterArguments(
                parseInput(inputValue), parseBaseUrl(baseUrlValue), parseThrough(throughValue));
    }

    private static Map<String, String> parseNamedValues(String[] arguments) {
        Map<String, String> values = new HashMap<>();

        for (int index = 0; index < arguments.length; index += 2) {
            String name = arguments[index];

            if (!SUPPORTED_ARGUMENTS.contains(name)) {
                throw new InvalidImporterArgumentsException("Unknown argument: " + name);
            }

            if (index + 1 >= arguments.length || arguments[index + 1].startsWith("--")) {
                throw new InvalidImporterArgumentsException("Missing value for argument: " + name);
            }

            String previousValue = values.putIfAbsent(name, arguments[index + 1]);
            if (previousValue != null) {
                throw new InvalidImporterArgumentsException("Duplicate argument: " + name);
            }
        }

        return values;
    }

    private static String requireArgument(Map<String, String> values, String name) {
        String value = values.get(name);
        if (value == null) {
            throw new InvalidImporterArgumentsException("Missing required argument: " + name);
        }
        return value;
    }

    private static Path parseInput(String value) {
        if (value.isBlank()) {
            throw new InvalidImporterArgumentsException(
                    "Invalid value for --input: path must not be blank");
        }

        try {
            return Path.of(value);
        } catch (InvalidPathException _) {
            throw new InvalidImporterArgumentsException(
                    "Invalid value for --input: path must not be blank");
        }
    }

    private static URI parseBaseUrl(String value) {
        URI baseUrl;
        try {
            baseUrl = URI.create(value);
        } catch (IllegalArgumentException _) {
            throw invalidBaseUrl();
        }

        String scheme = baseUrl.getScheme();
        boolean supportedScheme =
                "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);

        if (!baseUrl.isAbsolute() || !supportedScheme || baseUrl.getHost() == null) {
            throw invalidBaseUrl();
        }

        return baseUrl;
    }

    private static InvalidImporterArgumentsException invalidBaseUrl() {
        return new InvalidImporterArgumentsException(
                "Invalid value for --base-url: expected an absolute HTTP(S) URL");
    }

    private static LocalDate parseThrough(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException _) {
            throw new InvalidImporterArgumentsException(
                    "Invalid value for --through: expected YYYY-MM-DD");
        }
    }
}
