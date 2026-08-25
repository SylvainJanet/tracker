package fr.sylvainjanet.tracker.configuration.sqlite.environment;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextCustomizerFactories;

/** Use a custom SQLite database for the annotated test class. */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
@ExtendWith(SqliteTestDatabaseExtension.class)
@ContextCustomizerFactories(SqliteTestDatabaseContextCustomizerFactory.class)
public @interface SqliteTestDatabase {

    String value();
}
