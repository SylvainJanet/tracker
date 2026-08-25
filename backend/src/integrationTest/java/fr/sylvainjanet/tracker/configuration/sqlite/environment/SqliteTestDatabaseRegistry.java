package fr.sylvainjanet.tracker.configuration.sqlite.environment;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Ensures each database is created once. */
final class SqliteTestDatabaseRegistry {

    private static final Map<Class<?>, Registration> DATABASES_BY_TEST_CLASS = new HashMap<>();

    private static final Map<String, Class<?>> TEST_CLASSES_BY_DATABASE_NAME = new HashMap<>();

    private SqliteTestDatabaseRegistry() {}

    static synchronized TestSqliteDatabase databaseFor(Class<?> testClass, String databaseName) {

        Registration existingRegistration = DATABASES_BY_TEST_CLASS.get(testClass);

        if (existingRegistration != null) {
            if (!existingRegistration.databaseName().equals(databaseName)) {
                throw new IllegalStateException(
                        testClass.getName() + " requested multiple test databases");
            }

            return existingRegistration.database();
        }

        Class<?> existingOwner = TEST_CLASSES_BY_DATABASE_NAME.get(databaseName);

        if (existingOwner != null && !existingOwner.equals(testClass)) {
            throw new IllegalStateException(
                    "Test database '"
                            + databaseName
                            + "' is already owned by "
                            + existingOwner.getName());
        }

        TestSqliteDatabase database = TestSqliteDatabase.createFresh(databaseName);

        DATABASES_BY_TEST_CLASS.put(testClass, new Registration(databaseName, database));

        TEST_CLASSES_BY_DATABASE_NAME.put(databaseName, testClass);

        return database;
    }

    static Optional<TestDatabaseConfiguration> configurationFor(Class<?> testClass) {
        Class<?> candidate = testClass;

        while (candidate != null) {
            SqliteTestDatabase annotation =
                    candidate.getDeclaredAnnotation(SqliteTestDatabase.class);

            if (annotation != null) {
                return Optional.of(new TestDatabaseConfiguration(candidate, annotation.value()));
            }

            candidate = candidate.getEnclosingClass();
        }

        return Optional.empty();
    }

    record TestDatabaseConfiguration(Class<?> testClass, String databaseName) {}

    private record Registration(String databaseName, TestSqliteDatabase database) {}
}
