package fr.sylvainjanet.tracker.configuration.sqlite.safety;

import java.util.List;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.MergedContextConfiguration;

/** Installs production database protection in every Spring integration-test context. */
public final class ProductionDatabaseGuardContextCustomizerFactory
        implements ContextCustomizerFactory {

    @Override
    public ContextCustomizer createContextCustomizer(
            @NonNull Class<?> testClass,
            @NonNull List<ContextConfigurationAttributes> configAttributes) {
        return GuardContextCustomizer.INSTANCE;
    }

    private enum GuardContextCustomizer implements ContextCustomizer {
        INSTANCE;

        @Override
        public void customizeContext(
                @NonNull ConfigurableApplicationContext context,
                @NonNull MergedContextConfiguration mergedConfig) {
            context.addBeanFactoryPostProcessor(
                    new ProductionDatabaseGuard(context.getEnvironment()));
        }
    }
}
