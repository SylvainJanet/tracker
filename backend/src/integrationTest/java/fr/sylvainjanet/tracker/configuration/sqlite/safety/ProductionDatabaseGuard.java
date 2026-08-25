package fr.sylvainjanet.tracker.configuration.sqlite.safety;

import java.nio.file.Path;
import javax.sql.DataSource;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.Environment;

final class ProductionDatabaseGuard implements BeanFactoryPostProcessor {

    static final String PRODUCTION_DATABASE_URL = "jdbc:sqlite:./data/tracker.db";
    private static final String SQLITE_URL_PREFIX = "jdbc:sqlite:";
    static final String DEVELOPMENT_DATABASE_URL =
            SQLITE_URL_PREFIX + "./backend/build/dev-database/tracker.db";

    private final Environment environment;

    ProductionDatabaseGuard(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) {
        String[] dataSourceNames = beanFactory.getBeanNamesForType(DataSource.class, true, false);
        String configuredUrl = environment.getProperty("spring.datasource.url");
        boolean usesProductionDatabase =
                refersToSameDatabase(configuredUrl, PRODUCTION_DATABASE_URL);
        boolean usesDevelopmentDatabase =
                refersToSameDatabase(configuredUrl, DEVELOPMENT_DATABASE_URL);

        if (dataSourceNames.length > 0 && (usesProductionDatabase || usesDevelopmentDatabase)) {
            String databaseKind = usesProductionDatabase ? "production" : "development";

            throw new IllegalStateException(
                    "Spring integration tests must not use the "
                            + databaseKind
                            + " database "
                            + configuredUrl);
        }
    }

    private static boolean refersToSameDatabase(String configuredUrl, String protectedDatabaseUrl) {
        if (configuredUrl == null || !configuredUrl.startsWith(SQLITE_URL_PREFIX)) {
            return false;
        }

        Path configuredPath =
                Path.of(configuredUrl.substring(SQLITE_URL_PREFIX.length()))
                        .toAbsolutePath()
                        .normalize();
        Path protectedPath =
                Path.of(protectedDatabaseUrl.substring(SQLITE_URL_PREFIX.length()))
                        .toAbsolutePath()
                        .normalize();

        return configuredPath.equals(protectedPath);
    }
}
