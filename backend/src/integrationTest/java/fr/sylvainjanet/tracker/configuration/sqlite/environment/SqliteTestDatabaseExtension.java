package fr.sylvainjanet.tracker.configuration.sqlite.environment;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

/** Integrates the database registry with JUnit 5. */
public final class SqliteTestDatabaseExtension implements BeforeAllCallback, ParameterResolver {

    @Override
    public void beforeAll(@NonNull ExtensionContext context) {
        database(context);
    }

    @Override
    public boolean supportsParameter(
            @NonNull ParameterContext parameterContext,
            @NonNull ExtensionContext extensionContext) {

        return parameterContext.getParameter().getType() == TestSqliteDatabase.class;
    }

    @Override
    public Object resolveParameter(
            @NonNull ParameterContext parameterContext,
            @NonNull ExtensionContext extensionContext) {

        return database(extensionContext);
    }

    private static TestSqliteDatabase database(@NonNull ExtensionContext context) {

        SqliteTestDatabaseRegistry.TestDatabaseConfiguration configuration =
                SqliteTestDatabaseRegistry.configurationFor(context.getRequiredTestClass())
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "@SqliteTestDatabase is missing from "
                                                        + context.getRequiredTestClass()
                                                                .getName()));

        return SqliteTestDatabaseRegistry.databaseFor(
                configuration.testClass(), configuration.databaseName());
    }
}
