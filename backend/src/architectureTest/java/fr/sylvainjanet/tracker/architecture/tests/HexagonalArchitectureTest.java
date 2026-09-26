package fr.sylvainjanet.tracker.architecture.tests;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.conditions.ArchConditions.implement;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

@AnalyzeClasses(
        packages = "fr.sylvainjanet.tracker",
        importOptions = ImportOption.DoNotIncludeTests.class)
public class HexagonalArchitectureTest {

    private static final String CONTEXT_ROOT = "fr.sylvainjanet.tracker.*";

    private static final String DOMAIN = architecturePackage("domain");
    private static final String APPLICATION = architecturePackage("application");
    private static final String APPLICATION_SERVICES = architecturePackage("application.service");

    private static final String INBOUND_PORTS = architecturePackage("application.port.in");
    private static final String INBOUND_USE_CASES =
            architecturePackage("application.port.in.usecase");
    private static final String OUTBOUND_PORTS = architecturePackage("application.port.out");

    private static final String ADAPTER = architecturePackage("adapter");
    private static final String INBOUND_ADAPTERS = architecturePackage("adapter.in");
    private static final String WEB = architecturePackage("adapter.in.web");
    private static final String OUTBOUND_ADAPTERS = architecturePackage("adapter.out");
    private static final String PERSISTENCE = architecturePackage("adapter.out.persistence");
    private static final String OUTBOUND_ADAPTER_EXCEPTIONS =
            architecturePackage("adapter.out.persistence.exceptions");
    private static final String CONTEXT_CONFIGURATION = architecturePackage("configuration");
    private static final String APPLICATION_CONFIGURATION =
            "fr.sylvainjanet.tracker.configuration..";

    private static final Set<String> ALLOWED_CORE_JDK_PACKAGES =
            Set.of("java.lang", "java.time..", "java.util..", "java.math");
    private static final String[] ALLOWED_DOMAIN_DEPENDENCIES = allowedCoreDependencies(DOMAIN);
    private static final String[] ALLOWED_APPLICATION_DEPENDENCIES =
            allowedCoreDependencies(DOMAIN, APPLICATION);

    @ArchTest
    public static final ArchRule layersRespectHexagonalDependencies =
            layeredArchitecture()
                    .consideringOnlyDependenciesInLayers()
                    .layer("Domain")
                    .definedBy(DOMAIN)
                    .layer("Application")
                    .definedBy(APPLICATION)
                    .layer("Adapter")
                    .definedBy(ADAPTER)
                    .layer("Configuration")
                    .definedBy(CONTEXT_CONFIGURATION, APPLICATION_CONFIGURATION)
                    .whereLayer("Domain")
                    .mayOnlyBeAccessedByLayers("Application", "Adapter", "Configuration")
                    .whereLayer("Application")
                    .mayOnlyBeAccessedByLayers("Adapter", "Configuration")
                    .whereLayer("Adapter")
                    .mayOnlyBeAccessedByLayers("Configuration")
                    .whereLayer("Configuration")
                    .mayNotBeAccessedByAnyLayer();

    @ArchTest
    public static final ArchRule domainOnlyDependsOnDomainAndAllowedJdkPackages =
            classes()
                    .that()
                    .resideInAPackage(DOMAIN)
                    .should()
                    .onlyDependOnClassesThat()
                    .resideInAnyPackage(ALLOWED_DOMAIN_DEPENDENCIES);

    @ArchTest
    public static final ArchRule applicationOnlyDependsOnCoreAndAllowedJdkPackages =
            classes()
                    .that()
                    .resideInAPackage(APPLICATION)
                    .should()
                    .onlyDependOnClassesThat()
                    .resideInAnyPackage(ALLOWED_APPLICATION_DEPENDENCIES);

    @ArchTest
    public static final ArchRule webTechnologyStaysInWebAdapter =
            noClasses()
                    .that()
                    .resideOutsideOfPackage(WEB)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(
                            "org.springframework.web..",
                            "org.springframework.http..",
                            "jakarta.validation..",
                            "io.swagger.v3..");

    @ArchTest
    public static final ArchRule persistenceTechnologyStaysInPersistenceBoundary =
            noClasses()
                    .that()
                    .resideOutsideOfPackages(
                            PERSISTENCE, CONTEXT_CONFIGURATION, APPLICATION_CONFIGURATION)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(
                            "org.springframework.jdbc..",
                            "org.springframework.dao..",
                            "org.sqlite..",
                            "org.flywaydb..");

    @ArchTest
    public static final ArchRule inboundAdaptersDoNotBypassInboundPorts =
            noClasses()
                    .that()
                    .resideInAPackage(INBOUND_ADAPTERS)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(APPLICATION_SERVICES, OUTBOUND_PORTS);

    @ArchTest
    public static final ArchRule outboundAdaptersDoNotUseInboundSide =
            noClasses()
                    .that()
                    .resideInAPackage(OUTBOUND_ADAPTERS)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(INBOUND_PORTS, APPLICATION_SERVICES);

    @ArchTest
    public static final ArchRule applicationServicesImplementUseCases =
            classes()
                    .that()
                    .resideInAPackage(APPLICATION_SERVICES)
                    .and()
                    .haveSimpleNameEndingWith("Service")
                    .should(implement(resideInAPackage(INBOUND_USE_CASES)));

    @ArchTest
    public static final ArchRule outboundAdaptersImplementOutboundPorts =
            classes()
                    .that()
                    .resideInAPackage(OUTBOUND_ADAPTERS)
                    .and()
                    .resideOutsideOfPackage(OUTBOUND_ADAPTER_EXCEPTIONS)
                    .should(implement(resideInAPackage(OUTBOUND_PORTS)))
                    .allowEmptyShould(true);

    @ArchTest
    public static final ArchRule applicationPackagesAreFreeOfCycles =
            slices().matching(CONTEXT_ROOT + "..application.(*)..").should().beFreeOfCycles();

    private static String architecturePackage(String packagePath) {
        return CONTEXT_ROOT + ".." + packagePath + "..";
    }

    private static String[] allowedCoreDependencies(String... corePackages) {
        return Stream.concat(Arrays.stream(corePackages), ALLOWED_CORE_JDK_PACKAGES.stream())
                .toArray(String[]::new);
    }
}
