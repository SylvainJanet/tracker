package fr.sylvainjanet.tracker.architecture.tests;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@AnalyzeClasses(
        packages = "fr.sylvainjanet.tracker",
        importOptions = ImportOption.DoNotIncludeTests.class)
public class BoundedContextArchitectureTest {

    private static final String ROOT_PACKAGE = "fr.sylvainjanet.tracker";
    private static final String SHARED_KERNEL_PACKAGE = ROOT_PACKAGE + ".shared";
    private static final String SHARED_TECHNICAL_PACKAGE = ROOT_PACKAGE + ".technical";
    private static final Set<String> APPLICATION_LEVEL_PACKAGES = Set.of("configuration");

    @ArchTest
    public static final ArchRule contextsOnlyAccessOtherContextsThroughInboundContracts =
            classes()
                    .should(
                            new ArchCondition<>(
                                    "only access another bounded context through its inbound contracts") {
                                @Override
                                public void check(JavaClass source, ConditionEvents events) {
                                    Optional<String> sourceContext = boundedContextOf(source);

                                    if (sourceContext.isEmpty()) {
                                        return;
                                    }

                                    for (Dependency dependency :
                                            source.getDirectDependenciesFromSelf()) {
                                        JavaClass target = dependency.getTargetClass();

                                        if (isSharedKernel(target) || isSharedTechnical(target)) {
                                            continue;
                                        }

                                        Optional<String> targetContext = boundedContextOf(target);

                                        if (targetContext.isEmpty()
                                                || sourceContext.equals(targetContext)) {
                                            continue;
                                        }

                                        if (!isInboundContract(target)) {
                                            events.add(
                                                    SimpleConditionEvent.violated(
                                                            dependency,
                                                            dependency.getDescription()
                                                                    + " crosses a bounded-context boundary"
                                                                    + " outside the target context's"
                                                                    + " application.port.in contract: "
                                                                    + target.getName()));
                                        }
                                    }
                                }
                            });

    @ArchTest
    public static final ArchRule externallyUsedInboundContractsDoNotExposeContextInternals =
            classes()
                    .should(
                            new ArchCondition<>(
                                    "not expose context-internal types through externally used"
                                            + " inbound contracts") {
                                @Override
                                public void check(JavaClass candidate, ConditionEvents events) {
                                    if (!isInboundContract(candidate)
                                            || !isUsedByAnotherContext(candidate)) {
                                        return;
                                    }

                                    checkPublishedContractClosure(candidate, events);
                                }
                            });

    private static void checkPublishedContractClosure(
            JavaClass contractRoot, ConditionEvents events) {
        String owningContext = boundedContextOf(contractRoot).orElseThrow();

        ArrayDeque<JavaClass> remaining = new ArrayDeque<>();
        Set<String> inspectedClasses = new HashSet<>();

        remaining.add(contractRoot);

        while (!remaining.isEmpty()) {
            JavaClass contractType = remaining.removeFirst();

            if (!inspectedClasses.add(contractType.getName())) {
                continue;
            }

            for (Dependency dependency : contractType.getDirectDependenciesFromSelf()) {
                JavaClass target = dependency.getTargetClass();

                if (!isProjectClass(target)) {
                    continue;
                }

                Optional<String> targetContext = boundedContextOf(target);

                if (targetContext.isEmpty()
                        || !targetContext.get().equals(owningContext)
                        || !isInboundContract(target)) {
                    events.add(
                            SimpleConditionEvent.violated(
                                    dependency,
                                    "Published inbound contract "
                                            + contractType.getName()
                                            + " depends on "
                                            + target.getName()
                                            + ", which is outside its own application.port.in"
                                            + " contract"));
                    continue;
                }

                remaining.addLast(target);
            }
        }
    }

    private static boolean isSharedKernel(JavaClass javaClass) {
        String packageName = javaClass.getPackageName();

        return packageName.equals(SHARED_KERNEL_PACKAGE)
                || packageName.startsWith(SHARED_KERNEL_PACKAGE + ".");
    }

    private static boolean isSharedTechnical(JavaClass javaClass) {
        String packageName = javaClass.getPackageName();

        return packageName.equals(SHARED_TECHNICAL_PACKAGE)
                || packageName.startsWith(SHARED_TECHNICAL_PACKAGE + ".");
    }

    private static boolean isUsedByAnotherContext(JavaClass contractType) {
        Optional<String> owningContext = boundedContextOf(contractType);

        return owningContext.isPresent()
                && contractType.getDirectDependenciesToSelf().stream()
                        .map(Dependency::getOriginClass)
                        .map(BoundedContextArchitectureTest::boundedContextOf)
                        .flatMap(Optional::stream)
                        .anyMatch(context -> !context.equals(owningContext.get()));
    }

    private static boolean isInboundContract(JavaClass javaClass) {
        Optional<String> context = boundedContextOf(javaClass);

        if (context.isEmpty()) {
            return false;
        }

        String contextPackage = ROOT_PACKAGE + "." + context.get();
        String packageName = javaClass.getPackageName();

        if (!packageName.startsWith(contextPackage + ".")) {
            return false;
        }

        String relativePackage = packageName.substring(contextPackage.length() + 1);
        String[] segments = relativePackage.split("\\.");

        for (int index = 0; index < segments.length; index++) {
            if (!segments[index].equals("application")) {
                continue;
            }

            return index + 2 < segments.length
                    && segments[index + 1].equals("port")
                    && segments[index + 2].equals("in");
        }

        return false;
    }

    private static Optional<String> boundedContextOf(JavaClass javaClass) {
        String packageName = javaClass.getPackageName();
        String contextPrefix = ROOT_PACKAGE + ".";

        if (!packageName.startsWith(contextPrefix)) {
            return Optional.empty();
        }

        String relativePackage = packageName.substring(contextPrefix.length());
        int separator = relativePackage.indexOf('.');
        String context = separator < 0 ? relativePackage : relativePackage.substring(0, separator);

        return APPLICATION_LEVEL_PACKAGES.contains(context)
                ? Optional.empty()
                : Optional.of(context);
    }

    private static boolean isProjectClass(JavaClass javaClass) {
        String packageName = javaClass.getPackageName();

        return packageName.equals(ROOT_PACKAGE) || packageName.startsWith(ROOT_PACKAGE + ".");
    }
}
