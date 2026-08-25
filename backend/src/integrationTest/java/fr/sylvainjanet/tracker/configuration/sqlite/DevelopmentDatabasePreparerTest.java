package fr.sylvainjanet.tracker.configuration.sqlite;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DevelopmentDatabasePreparerTest {

    @TempDir Path directory;

    @Test
    void createsIndependentSnapshotIncludingCommittedWalChanges() throws Exception {
        var sourceDatabase = directory.resolve("source.db");
        var snapshotDatabase = directory.resolve("development/tracker.db");

        try (var sourceConnection = DriverManager.getConnection("jdbc:sqlite:" + sourceDatabase)) {
            try (var statement = sourceConnection.createStatement()) {
                statement.execute("PRAGMA journal_mode = WAL");
                statement.execute("CREATE TABLE sample (value TEXT NOT NULL)");
                statement.execute("INSERT INTO sample VALUES ('source value')");
            }

            DevelopmentDatabasePreparer.main(
                    new String[] {
                        "snapshot", sourceDatabase.toString(), snapshotDatabase.toString()
                    });

            assertThat(readValue(snapshotDatabase)).isEqualTo("source value");

            try (var snapshotConnection =
                            DriverManager.getConnection("jdbc:sqlite:" + snapshotDatabase);
                    var statement = snapshotConnection.createStatement()) {
                statement.execute("UPDATE sample SET value = 'development value'");
            }

            assertThat(readValue(sourceDatabase)).isEqualTo("source value");
        }
    }

    @org.junit.jupiter.api.Test
    void replacesSnapshotAndRemovesItsExistingSidecars() throws Exception {
        var sourceDatabase = directory.resolve("source.db");
        var snapshotDatabase = directory.resolve("development/tracker.db");

        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + sourceDatabase);
                var statement = connection.createStatement()) {
            statement.execute("CREATE TABLE sample (value TEXT NOT NULL)");
            statement.execute("INSERT INTO sample VALUES ('source value')");
        }

        Files.createDirectories(snapshotDatabase.getParent());
        Files.writeString(snapshotDatabase, "old database");
        Files.writeString(snapshotDatabase.resolveSibling("tracker.db-wal"), "old wal");
        Files.writeString(snapshotDatabase.resolveSibling("tracker.db-shm"), "old shm");
        Files.writeString(snapshotDatabase.resolveSibling("tracker.db-journal"), "old journal");

        DevelopmentDatabasePreparer.main(
                new String[] {"snapshot", sourceDatabase.toString(), snapshotDatabase.toString()});

        assertThat(readValue(snapshotDatabase)).isEqualTo("source value");
        assertThat(snapshotDatabase.resolveSibling("tracker.db-wal")).doesNotExist();
        assertThat(snapshotDatabase.resolveSibling("tracker.db-shm")).doesNotExist();
        assertThat(snapshotDatabase.resolveSibling("tracker.db-journal")).doesNotExist();
    }

    @Test
    void removesDevelopmentDatabaseAndItsSidecars() throws Exception {
        var developmentDatabase = directory.resolve("development/tracker.db");
        Files.createDirectories(developmentDatabase.getParent());
        Files.writeString(developmentDatabase, "database");
        Files.writeString(developmentDatabase.resolveSibling("tracker.db-wal"), "wal");
        Files.writeString(developmentDatabase.resolveSibling("tracker.db-shm"), "shm");
        Files.writeString(developmentDatabase.resolveSibling("tracker.db-journal"), "journal");

        DevelopmentDatabasePreparer.main(new String[] {"empty", developmentDatabase.toString()});

        assertThat(developmentDatabase).doesNotExist();
        assertThat(developmentDatabase.resolveSibling("tracker.db-wal")).doesNotExist();
        assertThat(developmentDatabase.resolveSibling("tracker.db-shm")).doesNotExist();
        assertThat(developmentDatabase.resolveSibling("tracker.db-journal")).doesNotExist();
    }

    private String readValue(Path database) throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + database);
                var statement = connection.createStatement();
                var result = statement.executeQuery("SELECT value FROM sample")) {
            return result.getString("value");
        }
    }
}
