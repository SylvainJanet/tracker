package fr.sylvainjanet.tracker.configuration.sqlite.environment;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/** Creates a fresh SQLite database for testing purposes. */
public record TestSqliteDatabase(String jdbcUrl, JdbcClient jdbcClient) {

    private static final Pattern VALID_DATABASE_NAME = Pattern.compile("[a-z0-9]+(?:-[a-z0-9]+)*");

    private static final Path DATABASE_DIRECTORY =
            Path.of("build", "test-databases").toAbsolutePath().normalize();

    private TestSqliteDatabase(String jdbcUrl, DataSource dataSource) {
        this(jdbcUrl, JdbcClient.create(dataSource));
    }

    static TestSqliteDatabase createFresh(String databaseName) {
        Path databasePath = databasePath(databaseName);
        prepareDatabaseFile(databasePath);

        String jdbcUrl = "jdbc:sqlite:" + databasePath;

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl(jdbcUrl);

        Flyway.configure().dataSource(dataSource).load().migrate();

        return new TestSqliteDatabase(jdbcUrl, dataSource);
    }

    private static Path databasePath(String databaseName) {
        validateName(databaseName);

        Path databasePath = DATABASE_DIRECTORY.resolve(databaseName + ".db").normalize();

        if (!DATABASE_DIRECTORY.equals(databasePath.getParent())) {
            throw new IllegalArgumentException(
                    "Test database must be directly under " + DATABASE_DIRECTORY);
        }

        return databasePath;
    }

    private static void validateName(String databaseName) {
        if (databaseName == null || !VALID_DATABASE_NAME.matcher(databaseName).matches()) {
            throw new IllegalArgumentException(
                    "Test database name must match " + VALID_DATABASE_NAME.pattern());
        }
    }

    private static void prepareDatabaseFile(Path databasePath) {
        try {
            Files.createDirectories(DATABASE_DIRECTORY);
            Files.deleteIfExists(databasePath);
        } catch (IOException exception) {
            throw new UncheckedIOException(
                    "Could not prepare test database " + databasePath, exception);
        }
    }
}
