package fr.sylvainjanet.tracker.configuration.sqlite.safety;

import static fr.sylvainjanet.tracker.configuration.sqlite.safety.ProductionDatabaseGuard.DEVELOPMENT_DATABASE_URL;
import static fr.sylvainjanet.tracker.configuration.sqlite.safety.ProductionDatabaseGuard.PRODUCTION_DATABASE_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.TestContextManager;

class ProductionDatabaseGuardTest {

    public static final String ABSOLUTE_PRODUCTION_DATABASE_URL =
            "jdbc:sqlite:" + Path.of("data", "tracker.db").toAbsolutePath().normalize();
    private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    @Test
    void defaultsToTheDisposableDevelopmentDatabase() throws IOException {
        Properties applicationProperties = new Properties();

        try (InputStream input =
                getClass().getClassLoader().getResourceAsStream("application.properties")) {
            assertThat(input).isNotNull();
            applicationProperties.load(input);
        }

        assertThat(applicationProperties.getProperty("spring.datasource.url"))
                .isEqualTo(DEVELOPMENT_DATABASE_URL);
    }

    @Test
    void rejectsProductionDatabaseBeforeInstantiatingTheDataSource() {
        beanFactory.registerBeanDefinition("dataSource", new RootBeanDefinition(DataSource.class));
        MockEnvironment environment =
                new MockEnvironment()
                        .withProperty("spring.datasource.url", PRODUCTION_DATABASE_URL);
        ProductionDatabaseGuard guard = new ProductionDatabaseGuard(environment);

        assertThatThrownBy(() -> guard.postProcessBeanFactory(beanFactory))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("production database")
                .hasMessageContaining(PRODUCTION_DATABASE_URL);

        assertThat(beanFactory.containsSingleton("dataSource")).isFalse();
    }

    @Test
    void rejectsAbsoluteProductionDatabaseBeforeInstantiatingTheDataSource() {
        beanFactory.registerBeanDefinition(
                "dataSource",
                BeanDefinitionBuilder.genericBeanDefinition(DataSource.class).getBeanDefinition());
        MockEnvironment environment =
                new MockEnvironment()
                        .withProperty("spring.datasource.url", ABSOLUTE_PRODUCTION_DATABASE_URL);
        ProductionDatabaseGuard guard = new ProductionDatabaseGuard(environment);

        assertThatThrownBy(() -> guard.postProcessBeanFactory(beanFactory))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("production database")
                .hasMessageContaining(ABSOLUTE_PRODUCTION_DATABASE_URL);
        assertThat(beanFactory.getSingletonCount()).isZero();
    }

    @Test
    void rejectsDevelopmentDatabaseBeforeInstantiatingTheDataSource() {
        beanFactory.registerBeanDefinition("dataSource", new RootBeanDefinition(DataSource.class));
        MockEnvironment environment =
                new MockEnvironment()
                        .withProperty("spring.datasource.url", DEVELOPMENT_DATABASE_URL);
        ProductionDatabaseGuard guard = new ProductionDatabaseGuard(environment);

        assertThatThrownBy(() -> guard.postProcessBeanFactory(beanFactory))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(DEVELOPMENT_DATABASE_URL);

        assertThat(beanFactory.containsSingleton("dataSource")).isFalse();
    }

    @Test
    void allowsAnExplicitNonProductionDatabase() {
        beanFactory.registerBeanDefinition("dataSource", new RootBeanDefinition(DataSource.class));
        MockEnvironment environment =
                new MockEnvironment()
                        .withProperty(
                                "spring.datasource.url",
                                "jdbc:postgresql://localhost/tracker-test");

        assertThatCode(
                        () ->
                                new ProductionDatabaseGuard(environment)
                                        .postProcessBeanFactory(beanFactory))
                .doesNotThrowAnyException();
    }

    @Test
    void allowsAContextWithoutADataSource() {
        MockEnvironment environment =
                new MockEnvironment()
                        .withProperty("spring.datasource.url", PRODUCTION_DATABASE_URL);

        assertThatCode(
                        () ->
                                new ProductionDatabaseGuard(environment)
                                        .postProcessBeanFactory(beanFactory))
                .doesNotThrowAnyException();
    }

    @Test
    void protectsSpringTestContextsBeforeTheirDataSourceIsCreated() {
        TestContextManager testContextManager =
                new TestContextManager(ProductionDatabaseContext.class);

        assertThatThrownBy(() -> testContextManager.getTestContext().getApplicationContext())
                .hasRootCauseInstanceOf(IllegalStateException.class)
                .hasRootCauseMessage(
                        "Spring integration tests must not use the production database "
                                + PRODUCTION_DATABASE_URL);
    }

    @SpringBootTest(
            classes = DataSourceContextConfiguration.class,
            properties = "spring.datasource.url=" + PRODUCTION_DATABASE_URL)
    private static class ProductionDatabaseContext {}

    @Configuration(proxyBeanMethods = false)
    private static class DataSourceContextConfiguration {

        @Bean
        DataSource dataSource() {
            throw new AssertionError("The production datasource must never be instantiated");
        }
    }
}
