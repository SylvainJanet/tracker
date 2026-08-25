package fr.sylvainjanet.tracker.configuration.sqlite.environment;

import java.util.List;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.MergedContextConfiguration;

/** Integrates the database with Spring tests * */
public final class SqliteTestDatabaseContextCustomizerFactory implements ContextCustomizerFactory {

    @Override
    public ContextCustomizer createContextCustomizer(
            @NonNull Class<?> testClass,
            @NonNull List<ContextConfigurationAttributes> configAttributes) {

        return SqliteTestDatabaseRegistry.configurationFor(testClass)
                .map(SqliteDatabaseContextCustomizer::new)
                .orElse(null);
    }

    private record SqliteDatabaseContextCustomizer(
            SqliteTestDatabaseRegistry.TestDatabaseConfiguration configuration)
            implements ContextCustomizer {

        @Override
        public void customizeContext(
                @NonNull ConfigurableApplicationContext context,
                @NonNull MergedContextConfiguration mergedConfig) {

            TestSqliteDatabase database =
                    SqliteTestDatabaseRegistry.databaseFor(
                            configuration.testClass(), configuration.databaseName());

            TestPropertyValues.of("spring.datasource.url=" + database.jdbcUrl()).applyTo(context);
        }
    }
}
