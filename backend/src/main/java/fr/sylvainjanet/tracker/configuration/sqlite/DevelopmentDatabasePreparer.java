package fr.sylvainjanet.tracker.configuration.sqlite;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DevelopmentDatabasePreparer {

    private DevelopmentDatabasePreparer() {}

    public static void main(String[] arguments) {
        if (arguments.length == 3 && arguments[0].equals("snapshot")) {
            snapshot(Path.of(arguments[1]), Path.of(arguments[2]));
            return;
        }

        if (arguments.length == 2 && arguments[0].equals("empty")) {
            empty(Path.of(arguments[1]));
            return;
        }

        throw new IllegalArgumentException(
                "Usage: DevelopmentDatabasePreparer "
                        + "snapshot <source> <destination> | empty <destination>");
    }

    static void empty(Path destination) {
        var normalizedDestination = destination.toAbsolutePath().normalize();

        try {
            Files.createDirectories(normalizedDestination.getParent());
            Files.deleteIfExists(normalizedDestination);
            deleteSidecars(normalizedDestination);
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not empty development database", exception);
        }
    }

    static void snapshot(Path source, Path destination) {
        var normalizedSource = source.toAbsolutePath().normalize();
        var normalizedDestination = destination.toAbsolutePath().normalize();

        if (normalizedSource.equals(normalizedDestination)) {
            throw new IllegalArgumentException(
                    "Source and development databases must be different");
        }

        if (!Files.isRegularFile(normalizedSource)) {
            throw new IllegalArgumentException(
                    "Source database does not exist: " + normalizedSource);
        }

        var destinationDirectory = normalizedDestination.getParent();
        Path temporarySnapshot = null;

        try {
            Files.createDirectories(destinationDirectory);
            temporarySnapshot =
                    Files.createTempFile(
                            destinationDirectory,
                            normalizedDestination.getFileName() + ".",
                            ".snapshot");
            Files.delete(temporarySnapshot);

            String vacuumQuery = "VACUUM INTO ?";
            try (var connection = DriverManager.getConnection("jdbc:sqlite:" + normalizedSource);
                    var statement = connection.prepareStatement(vacuumQuery)) {
                statement.setString(1, temporarySnapshot.toString());
                statement.execute();
            }

            deleteSidecars(normalizedDestination);
            replace(temporarySnapshot, normalizedDestination);
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not snapshot development database", exception);
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not prepare development database", exception);
        } finally {
            if (temporarySnapshot != null) {
                try {
                    Files.deleteIfExists(temporarySnapshot);
                } catch (IOException _) {
                    // Preserve the original failure.
                }
            }
        }
    }

    private static void deleteSidecars(Path database) throws IOException {
        Files.deleteIfExists(database.resolveSibling(database.getFileName() + "-wal"));
        Files.deleteIfExists(database.resolveSibling(database.getFileName() + "-shm"));
        Files.deleteIfExists(database.resolveSibling(database.getFileName() + "-journal"));
    }

    private static void replace(Path source, Path destination) throws IOException {
        try {
            Files.move(source, destination, ATOMIC_MOVE, REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException _) {
            Files.move(source, destination, REPLACE_EXISTING);
        }
    }
}
