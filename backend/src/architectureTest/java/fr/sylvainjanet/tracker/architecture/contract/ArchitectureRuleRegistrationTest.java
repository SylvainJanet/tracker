package fr.sylvainjanet.tracker.architecture.contract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;
import com.tngtech.archunit.lang.ArchRule;
import fr.sylvainjanet.tracker.architecture.contract.tests.BoundedContextArchitectureContractTest;
import fr.sylvainjanet.tracker.architecture.contract.tests.HexagonalArchitectureContractTest;
import fr.sylvainjanet.tracker.architecture.contract.tests.StructuralConventionContractTest;
import fr.sylvainjanet.tracker.architecture.contract.tests.StructuralInboundApplicationConventionContractTest;
import fr.sylvainjanet.tracker.architecture.contract.tests.StructuralOutboundApplicationConventionContractTest;
import fr.sylvainjanet.tracker.architecture.contract.tests.StructuralPersistenceConventionContractTest;
import fr.sylvainjanet.tracker.architecture.contract.tests.StructuralWebAdapterConventionContractTest;
import fr.sylvainjanet.tracker.architecture.tests.BoundedContextArchitectureTest;
import fr.sylvainjanet.tracker.architecture.tests.HexagonalArchitectureTest;
import fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class ArchitectureRuleRegistrationTest {

    private static final int EXPECTED_RULE_COUNT = 96;

    private static final List<Class<?>> ARCHITECTURE_POLICY_CLASSES =
            List.of(
                    BoundedContextArchitectureTest.class,
                    HexagonalArchitectureTest.class,
                    StructuralConventionTest.class);

    private static final List<Class<?>> ARCHITECTURE_CONTRACT_CLASSES =
            List.of(
                    BoundedContextArchitectureContractTest.class,
                    HexagonalArchitectureContractTest.class,
                    StructuralConventionContractTest.class,
                    StructuralInboundApplicationConventionContractTest.class,
                    StructuralOutboundApplicationConventionContractTest.class,
                    StructuralPersistenceConventionContractTest.class,
                    StructuralWebAdapterConventionContractTest.class);

    @Test
    void everyArchitectureRuleIsRegistered() {
        List<Field> ruleFields = fieldsOfType(ArchRule.class);

        assertEquals(EXPECTED_RULE_COUNT, ruleFields.size());
        for (Field ruleField : ruleFields) {
            assertTrue(
                    ruleField.isAnnotationPresent(ArchTest.class),
                    () -> fieldName(ruleField) + " must be annotated with @ArchTest");
        }
    }

    @Test
    void everyArchitectureRuleGroupIsRegistered() {
        List<Field> groupFields = fieldsOfType(ArchTests.class);

        assertTrue(!groupFields.isEmpty());
        for (Field groupField : groupFields) {
            assertTrue(
                    groupField.isAnnotationPresent(ArchTest.class),
                    () -> fieldName(groupField) + " must be annotated with @ArchTest");
        }
    }

    @Test
    void everyArchitectureRuleHasAnExplicitContractTest() {
        Set<String> ruleNames =
                fieldsOfType(ArchRule.class).stream()
                        .map(Field::getName)
                        .collect(java.util.stream.Collectors.toUnmodifiableSet());
        List<String> contractNames =
                ARCHITECTURE_CONTRACT_CLASSES.stream()
                        .flatMap(type -> Arrays.stream(type.getDeclaredMethods()))
                        .filter(method -> method.isAnnotationPresent(Test.class))
                        .map(Method::getName)
                        .toList();

        assertEquals(EXPECTED_RULE_COUNT, contractNames.size());
        assertEquals(ruleNames, Set.copyOf(contractNames));
    }

    private static List<Field> fieldsOfType(Class<?> fieldType) {
        return ARCHITECTURE_POLICY_CLASSES.stream()
                .flatMap(ArchitectureRuleRegistrationTest::classAndNestedClasses)
                .flatMap(type -> Arrays.stream(type.getDeclaredFields()))
                .filter(field -> fieldType.isAssignableFrom(field.getType()))
                .toList();
    }

    private static Stream<Class<?>> classAndNestedClasses(Class<?> type) {
        return Stream.concat(
                Stream.of(type),
                Arrays.stream(type.getDeclaredClasses())
                        .flatMap(ArchitectureRuleRegistrationTest::classAndNestedClasses));
    }

    private static String fieldName(Field field) {
        return field.getDeclaringClass().getSimpleName() + "." + field.getName();
    }
}
