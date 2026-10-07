package fr.sylvainjanet.tracker.architecture.tests;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainEnum;
import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainObject;
import fr.sylvainjanet.tracker.technical.domain.contract.base.EqualityExplicit;
import fr.sylvainjanet.tracker.technical.domain.contract.base.StringRepresentationExplicit;
import fr.sylvainjanet.tracker.technical.domain.contract.object.DomainAggregateObject;
import fr.sylvainjanet.tracker.technical.domain.contract.object.DomainEntityObject;
import fr.sylvainjanet.tracker.technical.domain.contract.object.DomainValueObject;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AnalyzeClasses(
        packages = "fr.sylvainjanet.tracker",
        importOptions = ImportOption.DoNotIncludeTests.class)
public class DomainContractArchitectureTest {

    private static final Set<Class<?>> SEMANTIC_DOMAIN_CONTRACTS =
            Set.of(DomainValueObject.class, DomainEntityObject.class, DomainAggregateObject.class);

    private static final Set<Class<?>> SELF_TYPED_ROOT_CONTRACTS =
            Set.of(DomainObject.class, DomainEnum.class);

    private static final DescribedPredicate<JavaClass> DOMAIN_OBJECT_IMPLEMENTATIONS =
            new DescribedPredicate<>("domain object implementations") {
                @Override
                public boolean test(JavaClass javaClass) {
                    return !javaClass.isInterface()
                            && SEMANTIC_DOMAIN_CONTRACTS.stream()
                                    .anyMatch(javaClass::isAssignableTo);
                }
            };

    private static final DescribedPredicate<JavaClass> DOMAIN_OBJECTS =
            new DescribedPredicate<>("domain objects") {
                @Override
                public boolean test(JavaClass javaClass) {
                    return !javaClass.isInterface() && javaClass.isAssignableTo(DomainObject.class);
                }
            };

    private static final DescribedPredicate<JavaClass> SELF_TYPED_DOMAIN_CONTRACT_IMPLEMENTATIONS =
            new DescribedPredicate<>("self-typed domain contract implementations") {
                @Override
                public boolean test(JavaClass javaClass) {
                    return !javaClass.isInterface()
                            && (javaClass.isAssignableTo(DomainObject.class)
                                    || javaClass.isAssignableTo(DomainEnum.class));
                }
            };

    @ArchTest
    public static final ArchRule domainObjectImplementationsBelongToDomainPackages =
            classes().that(DOMAIN_OBJECT_IMPLEMENTATIONS).should().resideInAPackage("..domain..");

    @ArchTest
    public static final ArchRule domainEnumsAreEnums =
            classes()
                    .that()
                    .areAssignableTo(DomainEnum.class)
                    .and()
                    .areNotInterfaces()
                    .should()
                    .beEnums();

    @ArchTest
    public static final ArchRule equalityExplicitTypesDeclareEqualsAndHashCode =
            classes()
                    .that()
                    .areAssignableTo(EqualityExplicit.class)
                    .and()
                    .areNotInterfaces()
                    .should(declareEqualsAndHashCode());

    @ArchTest
    public static final ArchRule stringRepresentationExplicitTypesDeclareToString =
            classes()
                    .that()
                    .areAssignableTo(StringRepresentationExplicit.class)
                    .and()
                    .areNotInterfaces()
                    .should(declareToString());

    @ArchTest
    public static final ArchRule selfTypedDomainContractsUseImplementingType =
            classes()
                    .that(SELF_TYPED_DOMAIN_CONTRACT_IMPLEMENTATIONS)
                    .should(useImplementingTypeAsSelfType());

    @ArchTest
    public static final ArchRule domainObjectsUseSemanticContracts =
            classes().that(DOMAIN_OBJECTS).should(implementSemanticDomainContract());

    private static ArchCondition<JavaClass> declareEqualsAndHashCode() {
        return new ArchCondition<>("declare equals(Object) and hashCode()") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                if (doesNotDeclareMethod(javaClass, "equals", Object.class)) {
                    addViolation(javaClass, events, "must declare equals(Object)");
                }
                if (doesNotDeclareMethod(javaClass, "hashCode")) {
                    addViolation(javaClass, events, "must declare hashCode()");
                }
            }
        };
    }

    private static ArchCondition<JavaClass> declareToString() {
        return new ArchCondition<>("declare toString()") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                if (doesNotDeclareMethod(javaClass, "toString")) {
                    addViolation(javaClass, events, "must declare toString()");
                }
            }
        };
    }

    private static ArchCondition<JavaClass> useImplementingTypeAsSelfType() {
        return new ArchCondition<>("use their implementing type as their self type") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                List<Type> selfTypes = selfTypesOf(javaClass.reflect());
                boolean allSelfTypesMatch =
                        !selfTypes.isEmpty()
                                && selfTypes.stream()
                                        .allMatch(
                                                selfType ->
                                                        rawTypeOf(selfType)
                                                                .equals(javaClass.reflect()));

                if (!allSelfTypesMatch) {
                    addViolation(
                            javaClass,
                            events,
                            "must use "
                                    + javaClass.getSimpleName()
                                    + " as the self type of its domain contracts");
                }
            }
        };
    }

    private static ArchCondition<JavaClass> implementSemanticDomainContract() {
        return new ArchCondition<>("implement a semantic domain contract") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                boolean implementsSemanticContract =
                        SEMANTIC_DOMAIN_CONTRACTS.stream().anyMatch(javaClass::isAssignableTo);

                if (!implementsSemanticContract) {
                    addViolation(
                            javaClass,
                            events,
                            "must implement DomainValueObject, DomainEntityObject, or"
                                    + " DomainAggregateObject");
                }
            }
        };
    }

    private static boolean doesNotDeclareMethod(
            JavaClass javaClass, String methodName, Class<?>... parameterTypes) {
        try {
            javaClass.reflect().getDeclaredMethod(methodName, parameterTypes);
            return false;
        } catch (NoSuchMethodException _) {
            return true;
        }
    }

    private static List<Type> selfTypesOf(Class<?> implementation) {
        List<Type> selfTypes = new ArrayList<>();
        collectSelfTypes(implementation, Map.of(), selfTypes);
        return selfTypes;
    }

    private static void collectSelfTypes(
            Type currentType, Map<TypeVariable<?>, Type> inheritedTypes, List<Type> selfTypes) {
        Class<?> rawType = rawTypeOf(currentType);
        Map<TypeVariable<?>, Type> resolvedTypes = new HashMap<>(inheritedTypes);

        if (currentType instanceof ParameterizedType parameterizedType) {
            TypeVariable<?>[] parameters = rawType.getTypeParameters();
            Type[] arguments = parameterizedType.getActualTypeArguments();

            for (int index = 0; index < parameters.length; index++) {
                resolvedTypes.put(parameters[index], resolve(arguments[index], inheritedTypes));
            }
        }

        if (SELF_TYPED_ROOT_CONTRACTS.contains(rawType)) {
            TypeVariable<?> selfParameter = rawType.getTypeParameters()[0];
            selfTypes.add(resolve(resolvedTypes.get(selfParameter), resolvedTypes));
        }

        for (Type genericInterface : rawType.getGenericInterfaces()) {
            collectSelfTypes(genericInterface, resolvedTypes, selfTypes);
        }
    }

    private static Type resolve(Type type, Map<TypeVariable<?>, Type> resolvedTypes) {
        Type resolved = type;

        while (resolved instanceof TypeVariable<?> && resolvedTypes.containsKey(resolved)) {
            resolved = resolvedTypes.get(resolved);
        }
        return resolved;
    }

    private static Class<?> rawTypeOf(Type type) {
        if (type instanceof Class<?> typeClass) {
            return typeClass;
        }
        if (type instanceof ParameterizedType parameterizedType
                && parameterizedType.getRawType() instanceof Class<?> rawType) {
            return rawType;
        }
        throw new IllegalArgumentException("Cannot resolve raw type of " + type);
    }

    private static void addViolation(
            JavaClass javaClass, ConditionEvents events, String violation) {
        events.add(SimpleConditionEvent.violated(javaClass, javaClass.getName() + " " + violation));
    }
}
