package fr.sylvainjanet.tracker.architecture.contract;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

public final class ArchitectureRuleContract {

    private ArchitectureRuleContract() {}

    public static void assertAccepts(ArchRule rule, Class<?>... fixtureClasses) {
        assertDoesNotThrow(() -> rule.check(importClasses(fixtureClasses)));
    }

    public static void assertRejects(
            ArchRule rule, String expectedViolationFragment, Class<?>... fixtureClasses) {
        JavaClasses classes = importClasses(fixtureClasses);
        AssertionError violation =
                assertThrows(
                        AssertionError.class,
                        () -> {
                            rule.check(classes);
                        });

        assertTrue(
                violation.getMessage() != null
                        && violation.getMessage().contains(expectedViolationFragment),
                () ->
                        "Expected architecture violation to contain \""
                                + expectedViolationFragment
                                + "\", but was:\n"
                                + violation.getMessage());
    }

    private static JavaClasses importClasses(Class<?>... fixtureClasses) {
        return new ClassFileImporter().importClasses(fixtureClasses);
    }
}
