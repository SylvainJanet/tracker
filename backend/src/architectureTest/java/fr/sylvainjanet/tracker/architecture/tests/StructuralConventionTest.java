package fr.sylvainjanet.tracker.architecture.tests;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.Set;

@AnalyzeClasses(
        packages = "fr.sylvainjanet.tracker",
        importOptions = ImportOption.DoNotIncludeTests.class)
public class StructuralConventionTest {

    private static final String ROOT_PACKAGE = "fr.sylvainjanet.tracker";
    private static final String CONTEXT_ROOT = ROOT_PACKAGE + ".*";

    private static final Set<String> ALLOWED_APPLICATION_CONFIGURATION_PACKAGES = Set.of("sqlite");
    private static final Set<String> ALLOWED_CONFIGURATION_CLASS_SUFFIXES =
            Set.of("Configuration", "Preparer");

    private static final Set<String> ALLOWED_CONTEXT_PACKAGES =
            Set.of("domain", "configuration", "adapter", "application");

    private static final Set<String> ALLOWED_ADAPTER_PACKAGES = Set.of("in", "out");

    private static final Set<String> ALLOWED_INBOUND_ADAPTER_PACKAGES = Set.of("web");
    private static final Set<String> ALLOWED_WEB_PACKAGES =
            Set.of("dtos", "validator", "handler", "controller");
    private static final Set<String> ALLOWED_WEB_DTO_PACKAGES = Set.of("request", "response");
    private static final Set<String> ALLOWED_REQUEST_PACKAGES = Set.of("enums");
    private static final Set<String> ALLOWED_RESPONSE_PACKAGES = Set.of("enums");

    private static final Set<String> ALLOWED_OUTBOUND_ADAPTER_PACKAGES = Set.of("persistence");
    private static final Set<String> ALLOWED_PERSISTENCE_PACKAGES =
            Set.of("exceptions", "repository");

    private static final Set<String> ALLOWED_APPLICATION_PACKAGES = Set.of("port", "service");

    private static final Set<String> ALLOWED_APPLICATION_PORT_PACKAGES = Set.of("in", "out");

    private static final Set<String> ALLOWED_INBOUND_PORT_PACKAGES =
            Set.of("usecase", "dtos", "exceptions");
    private static final Set<String> ALLOWED_INBOUND_PORT_DTO_PACKAGES =
            Set.of("command", "query", "result");

    private static final Set<String> ALLOWED_OUTBOUND_PORT_PACKAGES = Set.of("gateway", "dtos");
    private static final Set<String> ALLOWED_OUTBOUND_GATEWAY_PACKAGES = Set.of("store");
    private static final Set<String> ALLOWED_OUTBOUND_PORT_DTO_PACKAGES =
            Set.of("criteria", "instruction", "outcome");

    private static final String ADAPTERS = packageTree("adapter");

    private static final String INBOUND_ADAPTERS = packageTree("adapter.in");
    private static final String WEB = packageTree("adapter.in.web");
    private static final String WEB_CONTROLLER = packageTree("adapter.in.web.controller");
    private static final String WEB_DTOS = packageTree("adapter.in.web.dtos");
    private static final String REQUESTS = packageTree("adapter.in.web.dtos.request");
    private static final String RESPONSES = packageTree("adapter.in.web.dtos.response");
    private static final String REQUESTS_ENUMS = packageTree("adapter.in.web.dtos.request.enums");
    private static final String RESPONSES_ENUMS = packageTree("adapter.in.web.dtos.response.enums");
    private static final String WEB_HANDLER = packageTree("adapter.in.web.handler");
    private static final String WEB_VALIDATOR = packageTree("adapter.in.web.validator");

    private static final String OUTBOUND_ADAPTERS = packageTree("adapter.out");
    private static final String PERSISTENCE = packageTree("adapter.out.persistence");
    private static final String PERSISTENCE_EXCEPTIONS =
            packageTree("adapter.out.persistence.exceptions");
    private static final String PERSISTENCE_REPOSITORY =
            packageTree("adapter.out.persistence.repository");

    private static final String APPLICATION_CONFIGURATION = ROOT_PACKAGE + ".configuration..";
    private static final String CONTEXT_CONFIGURATION = packageTree("configuration");
    private static final String[] CONFIGURATION_PACKAGES = {
        APPLICATION_CONFIGURATION, CONTEXT_CONFIGURATION
    };

    private static final String APPLICATION = packageTree("application");
    private static final String APPLICATION_PORTS = packageTree("application.port");
    private static final String INBOUND_PORTS = packageTree("application.port.in");
    private static final String INBOUND_PORT_DTOS = packageTree("application.port.in.dtos");
    private static final String COMMANDS = packageTree("application.port.in.dtos.command");
    private static final String QUERIES = packageTree("application.port.in.dtos.query");
    private static final String RESULTS = packageTree("application.port.in.dtos.result");
    private static final String INBOUND_PORT_EXCEPTIONS =
            packageTree("application.port.in.exceptions");
    private static final String INBOUND_PORT_USE_CASES = packageTree("application.port.in.usecase");

    private static final String OUTBOUND_PORTS = packageTree("application.port.out");
    private static final String OUTBOUND_PORT_DTOS = packageTree("application.port.out.dtos");
    private static final String CRITERIA = packageTree("application.port.out.dtos.criteria");
    private static final String INSTRUCTION = packageTree("application.port.out.dtos.instruction");
    private static final String OUTCOME = packageTree("application.port.out.dtos.outcome");
    private static final String OUTBOUND_GATEWAYS = packageTree("application.port.out.gateway");
    private static final String STORE = packageTree("application.port.out.gateway.store");

    private static final String APPLICATION_SERVICES = packageTree("application.service");

    private static final String DOMAIN = packageTree("domain");

    @ArchTest static final ArchTests contextRules = ArchTests.in(ContextRules.class);

    public static final class ContextRules {

        @ArchTest
        public static final ArchRule contextClassesUseExactlyOneArchitecturalRole =
                classes()
                        .that()
                        .resideInAPackage(CONTEXT_ROOT + "..")
                        .and()
                        .resideOutsideOfPackage(APPLICATION_CONFIGURATION)
                        .should(haveExactlyOneArchitecturalRole());

        @ArchTest
        public static final ArchRule fieldInjectionIsForbidden =
                noFields()
                        .should()
                        .beAnnotatedWith("org.springframework.beans.factory.annotation.Autowired");

        @ArchTest
        static final ArchTests adapterRules =
                ArchTests.in(StructuralConventionTest.ContextRules.AdapterRules.class);

        public static final class AdapterRules {

            @ArchTest
            public static final ArchRule adaptersUseAllowedPackages =
                    classes()
                            .that()
                            .resideInAPackage(ADAPTERS)
                            .should()
                            .resideInAnyPackage(
                                    allowedSubpackageTrees(ADAPTERS, ALLOWED_ADAPTER_PACKAGES))
                            .allowEmptyShould(true);

            @ArchTest
            static final ArchTests adapterInboundRules =
                    ArchTests.in(
                            StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules
                                    .class);

            public static final class AdapterInboundRules {

                @ArchTest
                public static final ArchRule inboundAdaptersUseAllowedPackages =
                        classes()
                                .that()
                                .resideInAPackage(INBOUND_ADAPTERS)
                                .should()
                                .resideInAnyPackage(
                                        allowedSubpackageTrees(
                                                INBOUND_ADAPTERS, ALLOWED_INBOUND_ADAPTER_PACKAGES))
                                .allowEmptyShould(true);

                @ArchTest
                static final ArchTests webAdapterRules =
                        ArchTests.in(
                                StructuralConventionTest.ContextRules.AdapterRules
                                        .AdapterInboundRules.WebAdapterRules.class);

                public static final class WebAdapterRules {

                    @ArchTest
                    public static final ArchRule webAdaptersUseAllowedPackages =
                            classes()
                                    .that()
                                    .resideInAPackage(WEB)
                                    .should()
                                    .resideInAnyPackage(
                                            allowedSubpackageTrees(WEB, ALLOWED_WEB_PACKAGES))
                                    .allowEmptyShould(true);

                    @ArchTest
                    static final ArchTests webControllerRules =
                            ArchTests.in(
                                    StructuralConventionTest.ContextRules.AdapterRules
                                            .AdapterInboundRules.WebAdapterRules.WebControllerRules
                                            .class);

                    public static final class WebControllerRules {

                        @ArchTest
                        public static final ArchRule webControllersShouldHaveControllerSuffix =
                                classes()
                                        .that()
                                        .resideInAPackage(WEB_CONTROLLER)
                                        .and()
                                        .areTopLevelClasses()
                                        .should()
                                        .haveSimpleNameEndingWith("Controller")
                                        .allowEmptyShould(true);

                        @ArchTest
                        public static final ArchRule
                                classesNamedControllerStayInControllerPackages =
                                        classes()
                                                .that()
                                                .haveSimpleNameEndingWith("Controller")
                                                .should()
                                                .resideInAPackage(WEB_CONTROLLER)
                                                .allowEmptyShould(true);

                        @ArchTest
                        public static final ArchRule webControllersShouldBeFinalClasses =
                                classes()
                                        .that()
                                        .resideInAPackage(WEB_CONTROLLER)
                                        .and()
                                        .areTopLevelClasses()
                                        .should()
                                        .haveModifier(JavaModifier.FINAL)
                                        .allowEmptyShould(true);

                        @ArchTest
                        public static final ArchRule
                                webControllersShouldBeAnnotatedWithRestController =
                                        classes()
                                                .that()
                                                .resideInAPackage(WEB_CONTROLLER)
                                                .and()
                                                .areTopLevelClasses()
                                                .should()
                                                .beAnnotatedWith(
                                                        "org.springframework.web.bind.annotation.RestController")
                                                .allowEmptyShould(true);

                        @ArchTest
                        public static final ArchRule
                                webControllerEndpointsShouldDeclareOpenApiResponses =
                                        methods()
                                                .that()
                                                .areDeclaredInClassesThat()
                                                .resideInAPackage(WEB_CONTROLLER)
                                                .and()
                                                .areMetaAnnotatedWith(
                                                        "org.springframework.web.bind.annotation.RequestMapping")
                                                .should()
                                                .beAnnotatedWith(
                                                        "io.swagger.v3.oas.annotations.responses.ApiResponse")
                                                .orShould()
                                                .beAnnotatedWith(
                                                        "io.swagger.v3.oas.annotations.responses.ApiResponses")
                                                .allowEmptyShould(true);
                    }

                    @ArchTest
                    static final ArchTests webDtoRules =
                            ArchTests.in(
                                    StructuralConventionTest.ContextRules.AdapterRules
                                            .AdapterInboundRules.WebAdapterRules.WebDtoRules.class);

                    public static final class WebDtoRules {

                        @ArchTest
                        public static final ArchRule webDataTransferObjectsUseAllowedPackages =
                                classes()
                                        .that()
                                        .resideInAPackage(WEB_DTOS)
                                        .should()
                                        .resideInAnyPackage(
                                                allowedSubpackageTrees(
                                                        WEB_DTOS, ALLOWED_WEB_DTO_PACKAGES))
                                        .allowEmptyShould(true);

                        @ArchTest
                        public static final ArchRule webDataTransferObjectsDoNotDependOnCore =
                                noClasses()
                                        .that()
                                        .resideInAPackage(WEB_DTOS)
                                        .should()
                                        .dependOnClassesThat()
                                        .resideInAnyPackage(DOMAIN, APPLICATION)
                                        .allowEmptyShould(true);

                        @ArchTest
                        static final ArchTests requestRules =
                                ArchTests.in(
                                        StructuralConventionTest.ContextRules.AdapterRules
                                                .AdapterInboundRules.WebAdapterRules.WebDtoRules
                                                .RequestRules.class);

                        public static final class RequestRules {

                            @ArchTest
                            public static final ArchRule requestDtosUseAllowedPackages =
                                    classes()
                                            .that()
                                            .resideInAPackage(REQUESTS + "*")
                                            .should()
                                            .resideInAnyPackage(
                                                    allowedSubpackageTrees(
                                                            REQUESTS, ALLOWED_REQUEST_PACKAGES))
                                            .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule webRequestDtosHaveRequestSuffix =
                                    classes()
                                            .that()
                                            .resideInAPackage(REQUESTS)
                                            .should()
                                            .haveSimpleNameEndingWith("Request")
                                            .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule classesNamedRequestStayInRequestPackages =
                                    classes()
                                            .that()
                                            .haveSimpleNameEndingWith("Request")
                                            .should()
                                            .resideInAPackage(REQUESTS)
                                            .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule webRequestDtosShouldBeRecords =
                                    classes()
                                            .that()
                                            .resideInAPackage(REQUESTS)
                                            .and()
                                            .resideOutsideOfPackages(
                                                    allowedSubpackageTrees(
                                                            REQUESTS, ALLOWED_REQUEST_PACKAGES))
                                            .should()
                                            .beRecords()
                                            .allowEmptyShould(true);

                            @ArchTest
                            static final ArchTests requestEnumRules =
                                    ArchTests.in(
                                            StructuralConventionTest.ContextRules.AdapterRules
                                                    .AdapterInboundRules.WebAdapterRules.WebDtoRules
                                                    .RequestRules.RequestEnumRules.class);

                            public static final class RequestEnumRules {

                                @ArchTest
                                public static final ArchRule requestEnumsShouldBeEnums =
                                        classes()
                                                .that()
                                                .resideInAPackage(REQUESTS_ENUMS)
                                                .should()
                                                .beEnums()
                                                .allowEmptyShould(true);
                            }
                        }

                        @ArchTest
                        static final ArchTests responseRules =
                                ArchTests.in(
                                        StructuralConventionTest.ContextRules.AdapterRules
                                                .AdapterInboundRules.WebAdapterRules.WebDtoRules
                                                .ResponseRules.class);

                        public static final class ResponseRules {

                            @ArchTest
                            public static final ArchRule responseDtosUseAllowedPackages =
                                    classes()
                                            .that()
                                            .resideInAPackage(RESPONSES + "*")
                                            .should()
                                            .resideInAnyPackage(
                                                    allowedSubpackageTrees(
                                                            RESPONSES, ALLOWED_RESPONSE_PACKAGES))
                                            .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule webResponseDtosHaveResponseSuffix =
                                    classes()
                                            .that()
                                            .resideInAPackage(RESPONSES)
                                            .should()
                                            .haveSimpleNameEndingWith("Response")
                                            .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule
                                    classesNamedResponseStayInResponsePackages =
                                            classes()
                                                    .that()
                                                    .haveSimpleNameEndingWith("Response")
                                                    .should()
                                                    .resideInAPackage(RESPONSES)
                                                    .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule webResponseDtosShouldBeRecords =
                                    classes()
                                            .that()
                                            .resideInAPackage(RESPONSES)
                                            .and()
                                            .resideOutsideOfPackages(
                                                    allowedSubpackageTrees(
                                                            RESPONSES, ALLOWED_RESPONSE_PACKAGES))
                                            .should()
                                            .beRecords()
                                            .allowEmptyShould(true);

                            @ArchTest
                            static final ArchTests responseEnumRules =
                                    ArchTests.in(
                                            StructuralConventionTest.ContextRules.AdapterRules
                                                    .AdapterInboundRules.WebAdapterRules.WebDtoRules
                                                    .ResponseRules.ResponseEnumRules.class);

                            public static final class ResponseEnumRules {

                                @ArchTest
                                public static final ArchRule responseEnumsShouldBeEnums =
                                        classes()
                                                .that()
                                                .resideInAPackage(RESPONSES_ENUMS)
                                                .should()
                                                .beEnums()
                                                .allowEmptyShould(true);
                            }
                        }
                    }

                    @ArchTest
                    static final ArchTests webHandlerRules =
                            ArchTests.in(
                                    StructuralConventionTest.ContextRules.AdapterRules
                                            .AdapterInboundRules.WebAdapterRules.WebHandlerRules
                                            .class);

                    public static final class WebHandlerRules {

                        @ArchTest
                        public static final ArchRule webHandlerShouldHaveHandlerSuffix =
                                classes()
                                        .that()
                                        .resideInAPackage(WEB_HANDLER)
                                        .should()
                                        .haveSimpleNameEndingWith("Handler")
                                        .allowEmptyShould(true);

                        @ArchTest
                        public static final ArchRule classesNamedHandlerStayInHandlerPackages =
                                classes()
                                        .that()
                                        .haveSimpleNameEndingWith("Handler")
                                        .should()
                                        .resideInAPackage(WEB_HANDLER)
                                        .allowEmptyShould(true);

                        @ArchTest
                        public static final ArchRule webHandlersShouldBeFinalClasses =
                                classes()
                                        .that()
                                        .resideInAPackage(WEB_HANDLER)
                                        .should()
                                        .haveModifier(JavaModifier.FINAL)
                                        .allowEmptyShould(true);

                        @ArchTest
                        public static final ArchRule
                                webHandlersShouldBeAnnotatedWithRestControllerAdvice =
                                        classes()
                                                .that()
                                                .resideInAPackage(WEB_HANDLER)
                                                .should()
                                                .beAnnotatedWith(
                                                        "org.springframework.web.bind.annotation.RestControllerAdvice")
                                                .allowEmptyShould(true);

                        @ArchTest
                        public static final ArchRule
                                exceptionHandlerMethodsShouldReturnProblemDetails =
                                        methods()
                                                .that()
                                                .areDeclaredInClassesThat()
                                                .resideInAPackage(WEB_HANDLER)
                                                .and()
                                                .areAnnotatedWith(
                                                        "org.springframework.web.bind.annotation.ExceptionHandler")
                                                .should()
                                                .haveRawReturnType(
                                                        "org.springframework.http.ProblemDetail")
                                                .allowEmptyShould(true);
                    }

                    @ArchTest
                    static final ArchTests webValidatorRules =
                            ArchTests.in(
                                    StructuralConventionTest.ContextRules.AdapterRules
                                            .AdapterInboundRules.WebAdapterRules.WebValidatorRules
                                            .class);

                    public static final class WebValidatorRules {

                        @ArchTest
                        public static final ArchRule webValidatorShouldHaveValidatorSuffix =
                                classes()
                                        .that()
                                        .resideInAPackage(WEB_VALIDATOR)
                                        .should()
                                        .haveSimpleNameEndingWith("Validator")
                                        .allowEmptyShould(true);

                        @ArchTest
                        public static final ArchRule classesNamedValidatorStayInValidatorPackages =
                                classes()
                                        .that()
                                        .haveSimpleNameEndingWith("Validator")
                                        .should()
                                        .resideInAPackage(WEB_VALIDATOR)
                                        .allowEmptyShould(true);
                    }
                }
            }

            @ArchTest
            static final ArchTests adapterOutboundRules =
                    ArchTests.in(
                            StructuralConventionTest.ContextRules.AdapterRules.AdapterOutboundRules
                                    .class);

            public static final class AdapterOutboundRules {

                @ArchTest
                public static final ArchRule outboundAdaptersUseAllowedPackages =
                        classes()
                                .that()
                                .resideInAPackage(OUTBOUND_ADAPTERS)
                                .should()
                                .resideInAnyPackage(
                                        allowedSubpackageTrees(
                                                OUTBOUND_ADAPTERS,
                                                ALLOWED_OUTBOUND_ADAPTER_PACKAGES))
                                .allowEmptyShould(true);

                @ArchTest
                static final ArchTests persistenceRules =
                        ArchTests.in(
                                StructuralConventionTest.ContextRules.AdapterRules
                                        .AdapterOutboundRules.PersistenceRules.class);

                public static final class PersistenceRules {

                    @ArchTest
                    public static final ArchRule persistenceAdaptersUseAllowedPackages =
                            classes()
                                    .that()
                                    .resideInAPackage(PERSISTENCE)
                                    .should()
                                    .resideInAnyPackage(
                                            allowedSubpackageTrees(
                                                    PERSISTENCE, ALLOWED_PERSISTENCE_PACKAGES))
                                    .allowEmptyShould(true);

                    @ArchTest
                    static final ArchTests persistenceExceptionsRules =
                            ArchTests.in(
                                    StructuralConventionTest.ContextRules.AdapterRules
                                            .AdapterOutboundRules.PersistenceRules
                                            .PersistenceExceptionsRules.class);

                    public static final class PersistenceExceptionsRules {

                        @ArchTest
                        public static final ArchRule persistenceExceptionsHaveExceptionSuffix =
                                classes()
                                        .that()
                                        .resideInAPackage(PERSISTENCE_EXCEPTIONS)
                                        .should()
                                        .haveSimpleNameEndingWith("Exception")
                                        .allowEmptyShould(true);

                        @ArchTest
                        public static final ArchRule persistenceExceptionsExtendRuntimeException =
                                classes()
                                        .that()
                                        .resideInAPackage(PERSISTENCE_EXCEPTIONS)
                                        .should()
                                        .beAssignableTo(RuntimeException.class)
                                        .allowEmptyShould(true);
                    }

                    @ArchTest
                    static final ArchTests persistenceRepositoryRules =
                            ArchTests.in(
                                    StructuralConventionTest.ContextRules.AdapterRules
                                            .AdapterOutboundRules.PersistenceRules
                                            .PersistenceRepositoryRules.class);

                    public static final class PersistenceRepositoryRules {

                        @ArchTest
                        public static final ArchRule persistenceRepositoriesHaveRepositorySuffix =
                                classes()
                                        .that()
                                        .resideInAPackage(PERSISTENCE_REPOSITORY)
                                        .should()
                                        .haveSimpleNameEndingWith("Repository")
                                        .allowEmptyShould(true);

                        @ArchTest
                        public static final ArchRule
                                classesNamedRepositoryStayInRepositoryPackages =
                                        classes()
                                                .that()
                                                .haveSimpleNameEndingWith("Repository")
                                                .should()
                                                .resideInAPackage(PERSISTENCE_REPOSITORY)
                                                .allowEmptyShould(true);

                        @ArchTest
                        public static final ArchRule persistenceRepositoriesShouldBeFinalClasses =
                                classes()
                                        .that()
                                        .resideInAPackage(PERSISTENCE_REPOSITORY)
                                        .should()
                                        .haveModifier(JavaModifier.FINAL)
                                        .allowEmptyShould(true);
                    }
                }
            }
        }

        @ArchTest
        static final ArchTests applicationRules =
                ArchTests.in(StructuralConventionTest.ContextRules.ApplicationRules.class);

        public static final class ApplicationRules {

            @ArchTest
            public static final ArchRule applicationUsesAllowedPackages =
                    classes()
                            .that()
                            .resideInAPackage(APPLICATION)
                            .should()
                            .resideInAnyPackage(
                                    allowedSubpackageTrees(
                                            APPLICATION, ALLOWED_APPLICATION_PACKAGES))
                            .allowEmptyShould(true);

            @ArchTest
            static final ArchTests portRules =
                    ArchTests.in(
                            StructuralConventionTest.ContextRules.ApplicationRules.PortRules.class);

            public static final class PortRules {

                @ArchTest
                public static final ArchRule applicationPortsUseAllowedPackages =
                        classes()
                                .that()
                                .resideInAPackage(APPLICATION_PORTS)
                                .should()
                                .resideInAnyPackage(
                                        allowedSubpackageTrees(
                                                APPLICATION_PORTS,
                                                ALLOWED_APPLICATION_PORT_PACKAGES))
                                .allowEmptyShould(true);

                @ArchTest
                static final ArchTests inboundPortRules =
                        ArchTests.in(
                                StructuralConventionTest.ContextRules.ApplicationRules.PortRules
                                        .InboundPortRules.class);

                public static final class InboundPortRules {

                    @ArchTest
                    public static final ArchRule inboundPortsUseAllowedPackages =
                            classes()
                                    .that()
                                    .resideInAPackage(INBOUND_PORTS)
                                    .should()
                                    .resideInAnyPackage(
                                            allowedSubpackageTrees(
                                                    INBOUND_PORTS, ALLOWED_INBOUND_PORT_PACKAGES))
                                    .allowEmptyShould(true);

                    @ArchTest
                    static final ArchTests dtosRules =
                            ArchTests.in(
                                    StructuralConventionTest.ContextRules.ApplicationRules.PortRules
                                            .InboundPortRules.DtosRules.class);

                    public static final class DtosRules {

                        @ArchTest
                        public static final ArchRule inboundPortDtosUseAllowedPackages =
                                classes()
                                        .that()
                                        .resideInAPackage(INBOUND_PORT_DTOS)
                                        .should()
                                        .resideInAnyPackage(
                                                allowedSubpackageTrees(
                                                        INBOUND_PORT_DTOS,
                                                        ALLOWED_INBOUND_PORT_DTO_PACKAGES))
                                        .allowEmptyShould(true);

                        @ArchTest
                        static final ArchTests commandRules =
                                ArchTests.in(
                                        StructuralConventionTest.ContextRules.ApplicationRules
                                                .PortRules.InboundPortRules.DtosRules.CommandRules
                                                .class);

                        public static final class CommandRules {

                            @ArchTest
                            public static final ArchRule commandDtosHaveCommandSuffix =
                                    classes()
                                            .that()
                                            .resideInAPackage(COMMANDS)
                                            .should()
                                            .haveSimpleNameEndingWith("Command")
                                            .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule classesNamedCommandStayInCommandPackages =
                                    classes()
                                            .that()
                                            .haveSimpleNameEndingWith("Command")
                                            .should()
                                            .resideInAPackage(COMMANDS)
                                            .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule commandDtosAreRecordsOrEnums =
                                    classes()
                                            .that()
                                            .resideInAPackage(COMMANDS)
                                            .should()
                                            .beRecords()
                                            .orShould()
                                            .beEnums()
                                            .allowEmptyShould(true);
                        }

                        @ArchTest
                        static final ArchTests queryRules =
                                ArchTests.in(
                                        StructuralConventionTest.ContextRules.ApplicationRules
                                                .PortRules.InboundPortRules.DtosRules.QueryRules
                                                .class);

                        public static final class QueryRules {

                            @ArchTest
                            public static final ArchRule queryDtosHaveQuerySuffix =
                                    classes()
                                            .that()
                                            .resideInAPackage(QUERIES)
                                            .should()
                                            .haveSimpleNameEndingWith("Query")
                                            .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule classesNamedQueryStayInQueryPackages =
                                    classes()
                                            .that()
                                            .haveSimpleNameEndingWith("Query")
                                            .should()
                                            .resideInAPackage(QUERIES)
                                            .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule queryDtosAreRecordsOrEnums =
                                    classes()
                                            .that()
                                            .resideInAPackage(QUERIES)
                                            .should()
                                            .beRecords()
                                            .orShould()
                                            .beEnums()
                                            .allowEmptyShould(true);
                        }

                        @ArchTest
                        static final ArchTests resultRules =
                                ArchTests.in(
                                        StructuralConventionTest.ContextRules.ApplicationRules
                                                .PortRules.InboundPortRules.DtosRules.ResultRules
                                                .class);

                        public static final class ResultRules {

                            @ArchTest
                            public static final ArchRule resultDtosHaveResultSuffix =
                                    classes()
                                            .that()
                                            .resideInAPackage(RESULTS)
                                            .should()
                                            .haveSimpleNameEndingWith("Result")
                                            .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule classesNamedResultStayInResultPackages =
                                    classes()
                                            .that()
                                            .haveSimpleNameEndingWith("Result")
                                            .should()
                                            .resideInAPackage(RESULTS)
                                            .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule resultDtosAreRecordsOrEnums =
                                    classes()
                                            .that()
                                            .resideInAPackage(RESULTS)
                                            .should()
                                            .beRecords()
                                            .orShould()
                                            .beEnums()
                                            .allowEmptyShould(true);
                        }
                    }

                    @ArchTest
                    static final ArchTests exceptionsRules =
                            ArchTests.in(
                                    StructuralConventionTest.ContextRules.ApplicationRules.PortRules
                                            .InboundPortRules.ExceptionsRules.class);

                    public static final class ExceptionsRules {

                        @ArchTest
                        public static final ArchRule inboundPortExceptionsHaveExceptionSuffix =
                                classes()
                                        .that()
                                        .resideInAPackage(INBOUND_PORT_EXCEPTIONS)
                                        .should()
                                        .haveSimpleNameEndingWith("Exception")
                                        .allowEmptyShould(true);

                        @ArchTest
                        public static final ArchRule inboundPortExceptionsAreChecked =
                                classes()
                                        .that()
                                        .resideInAPackage(INBOUND_PORT_EXCEPTIONS)
                                        .should()
                                        .beAssignableTo(Exception.class)
                                        .andShould()
                                        .notBeAssignableTo(RuntimeException.class)
                                        .allowEmptyShould(true);
                    }

                    @ArchTest
                    static final ArchTests usecaseRules =
                            ArchTests.in(
                                    StructuralConventionTest.ContextRules.ApplicationRules.PortRules
                                            .InboundPortRules.UsecaseRules.class);

                    public static final class UsecaseRules {

                        @ArchTest
                        public static final ArchRule inboundPortUseCasesHaveUseCaseSuffix =
                                classes()
                                        .that()
                                        .resideInAPackage(INBOUND_PORT_USE_CASES)
                                        .should()
                                        .haveSimpleNameEndingWith("UseCase")
                                        .allowEmptyShould(true);

                        @ArchTest
                        public static final ArchRule classesNamedUseCaseStayInUseCasePackages =
                                classes()
                                        .that()
                                        .haveSimpleNameEndingWith("UseCase")
                                        .should()
                                        .resideInAPackage(INBOUND_PORT_USE_CASES)
                                        .allowEmptyShould(true);

                        @ArchTest
                        public static final ArchRule inboundPortUseCasesAreInterfaces =
                                classes()
                                        .that()
                                        .resideInAPackage(INBOUND_PORT_USE_CASES)
                                        .should()
                                        .beInterfaces()
                                        .allowEmptyShould(true);
                    }
                }

                @ArchTest
                static final ArchTests outboundPortRules =
                        ArchTests.in(
                                StructuralConventionTest.ContextRules.ApplicationRules.PortRules
                                        .OutboundPortRules.class);

                public static final class OutboundPortRules {

                    @ArchTest
                    public static final ArchRule outboundPortsUseAllowedPackages =
                            classes()
                                    .that()
                                    .resideInAPackage(OUTBOUND_PORTS)
                                    .should()
                                    .resideInAnyPackage(
                                            allowedSubpackageTrees(
                                                    OUTBOUND_PORTS, ALLOWED_OUTBOUND_PORT_PACKAGES))
                                    .allowEmptyShould(true);

                    @ArchTest
                    static final ArchTests dtosRules =
                            ArchTests.in(
                                    StructuralConventionTest.ContextRules.ApplicationRules.PortRules
                                            .OutboundPortRules.DtosRules.class);

                    public static final class DtosRules {

                        @ArchTest
                        public static final ArchRule outboundPortDtosUseAllowedPackages =
                                classes()
                                        .that()
                                        .resideInAPackage(OUTBOUND_PORT_DTOS)
                                        .should()
                                        .resideInAnyPackage(
                                                allowedSubpackageTrees(
                                                        OUTBOUND_PORT_DTOS,
                                                        ALLOWED_OUTBOUND_PORT_DTO_PACKAGES))
                                        .allowEmptyShould(true);

                        @ArchTest
                        static final ArchTests criteriaRules =
                                ArchTests.in(
                                        StructuralConventionTest.ContextRules.ApplicationRules
                                                .PortRules.OutboundPortRules.DtosRules.CriteriaRules
                                                .class);

                        public static final class CriteriaRules {

                            @ArchTest
                            public static final ArchRule criteriaDtosHaveCriteriaSuffix =
                                    classes()
                                            .that()
                                            .resideInAPackage(CRITERIA)
                                            .should()
                                            .haveSimpleNameEndingWith("Criteria")
                                            .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule
                                    classesNamedCriteriaStayInCriteriaPackages =
                                            classes()
                                                    .that()
                                                    .haveSimpleNameEndingWith("Criteria")
                                                    .should()
                                                    .resideInAPackage(CRITERIA)
                                                    .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule criteriaDtosAreRecordsOrEnums =
                                    classes()
                                            .that()
                                            .resideInAPackage(CRITERIA)
                                            .should()
                                            .beRecords()
                                            .orShould()
                                            .beEnums()
                                            .allowEmptyShould(true);
                        }

                        @ArchTest
                        static final ArchTests instructionRules =
                                ArchTests.in(
                                        StructuralConventionTest.ContextRules.ApplicationRules
                                                .PortRules.OutboundPortRules.DtosRules
                                                .InstructionRules.class);

                        public static final class InstructionRules {

                            @ArchTest
                            public static final ArchRule instructionDtosHaveInstructionSuffix =
                                    classes()
                                            .that()
                                            .resideInAPackage(INSTRUCTION)
                                            .should()
                                            .haveSimpleNameEndingWith("Instruction")
                                            .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule
                                    classesNamedInstructionStayInInstructionPackages =
                                            classes()
                                                    .that()
                                                    .haveSimpleNameEndingWith("Instruction")
                                                    .should()
                                                    .resideInAPackage(INSTRUCTION)
                                                    .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule instructionDtosAreRecordsOrEnums =
                                    classes()
                                            .that()
                                            .resideInAPackage(INSTRUCTION)
                                            .should()
                                            .beRecords()
                                            .orShould()
                                            .beEnums()
                                            .allowEmptyShould(true);
                        }

                        @ArchTest
                        static final ArchTests outcomeRules =
                                ArchTests.in(
                                        StructuralConventionTest.ContextRules.ApplicationRules
                                                .PortRules.OutboundPortRules.DtosRules.OutcomeRules
                                                .class);

                        public static final class OutcomeRules {

                            @ArchTest
                            public static final ArchRule outcomeDtosHaveOutcomeSuffix =
                                    classes()
                                            .that()
                                            .resideInAPackage(OUTCOME)
                                            .should()
                                            .haveSimpleNameEndingWith("Outcome")
                                            .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule classesNamedOutcomeStayInOutcomePackages =
                                    classes()
                                            .that()
                                            .haveSimpleNameEndingWith("Outcome")
                                            .should()
                                            .resideInAPackage(OUTCOME)
                                            .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule outcomeDtosAreRecordsOrEnums =
                                    classes()
                                            .that()
                                            .resideInAPackage(OUTCOME)
                                            .should()
                                            .beRecords()
                                            .orShould()
                                            .beEnums()
                                            .allowEmptyShould(true);
                        }
                    }

                    @ArchTest
                    static final ArchTests gatewayRules =
                            ArchTests.in(
                                    StructuralConventionTest.ContextRules.ApplicationRules.PortRules
                                            .OutboundPortRules.GatewayRules.class);

                    public static final class GatewayRules {

                        @ArchTest
                        public static final ArchRule outboundGatewaysUseAllowedPackages =
                                classes()
                                        .that()
                                        .resideInAPackage(OUTBOUND_GATEWAYS)
                                        .should()
                                        .resideInAnyPackage(
                                                allowedSubpackageTrees(
                                                        OUTBOUND_GATEWAYS,
                                                        ALLOWED_OUTBOUND_GATEWAY_PACKAGES))
                                        .allowEmptyShould(true);

                        @ArchTest
                        static final ArchTests storeRules =
                                ArchTests.in(
                                        StructuralConventionTest.ContextRules.ApplicationRules
                                                .PortRules.OutboundPortRules.GatewayRules.StoreRules
                                                .class);

                        public static final class StoreRules {

                            @ArchTest
                            public static final ArchRule outboundStoresHaveStoreSuffix =
                                    classes()
                                            .that()
                                            .resideInAPackage(STORE)
                                            .should()
                                            .haveSimpleNameEndingWith("Store")
                                            .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule classesNamedStoreStayInStorePackages =
                                    classes()
                                            .that()
                                            .haveSimpleNameEndingWith("Store")
                                            .should()
                                            .resideInAPackage(STORE)
                                            .allowEmptyShould(true);

                            @ArchTest
                            public static final ArchRule outboundStoresShouldBeInterfaces =
                                    classes()
                                            .that()
                                            .resideInAPackage(STORE)
                                            .should()
                                            .beInterfaces();
                        }
                    }
                }
            }

            @ArchTest
            static final ArchTests serviceRules =
                    ArchTests.in(
                            StructuralConventionTest.ContextRules.ApplicationRules.ServiceRules
                                    .class);

            public static final class ServiceRules {
                @ArchTest
                public static final ArchRule servicesHaveServiceSuffix =
                        classes()
                                .that()
                                .resideInAPackage(APPLICATION_SERVICES)
                                .and()
                                .areTopLevelClasses()
                                .should()
                                .haveSimpleNameEndingWith("Service")
                                .allowEmptyShould(true);

                @ArchTest
                public static final ArchRule classesNamedServiceStayInServicePackages =
                        classes()
                                .that()
                                .haveSimpleNameEndingWith("Service")
                                .should()
                                .resideInAPackage(APPLICATION_SERVICES)
                                .allowEmptyShould(true);

                @ArchTest
                public static final ArchRule servicesShouldBeFinal =
                        classes()
                                .that()
                                .resideInAPackage(APPLICATION_SERVICES)
                                .and()
                                .areTopLevelClasses()
                                .should()
                                .haveModifier(JavaModifier.FINAL);
            }
        }

        @ArchTest
        static final ArchTests configurationRules =
                ArchTests.in(StructuralConventionTest.ContextRules.ConfigurationRules.class);

        public static final class ConfigurationRules {
            @ArchTest
            public static final ArchRule applicationConfigurationUsesAllowedPackages =
                    classes()
                            .that()
                            .resideInAPackage(APPLICATION_CONFIGURATION)
                            .should()
                            .resideInAnyPackage(
                                    allowedSubpackageTrees(
                                            APPLICATION_CONFIGURATION,
                                            ALLOWED_APPLICATION_CONFIGURATION_PACKAGES))
                            .allowEmptyShould(true);

            @ArchTest
            public static final ArchRule configurationClassesHaveAllowedSuffixes =
                    classes()
                            .that()
                            .resideInAnyPackage(CONFIGURATION_PACKAGES)
                            .and()
                            .areTopLevelClasses()
                            .should(haveAnAllowedConfigurationSuffix())
                            .allowEmptyShould(true);

            @ArchTest
            public static final ArchRule configurationClassesStayInConfigurationPackage =
                    classes()
                            .that()
                            .areAnnotatedWith(
                                    "org.springframework.context.annotation.Configuration")
                            .should()
                            .resideInAnyPackage(CONFIGURATION_PACKAGES)
                            .allowEmptyShould(true);
        }
    }

    private static ArchCondition<JavaClass> haveAnAllowedConfigurationSuffix() {
        return new ArchCondition<>(
                "have a simple name ending with one of " + ALLOWED_CONFIGURATION_CLASS_SUFFIXES) {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                boolean hasAllowedSuffix =
                        ALLOWED_CONFIGURATION_CLASS_SUFFIXES.stream()
                                .anyMatch(javaClass.getSimpleName()::endsWith);

                if (!hasAllowedSuffix) {
                    events.add(
                            SimpleConditionEvent.violated(
                                    javaClass,
                                    javaClass.getName()
                                            + " must end with one of "
                                            + ALLOWED_CONFIGURATION_CLASS_SUFFIXES));
                }
            }
        };
    }

    private static ArchCondition<JavaClass> haveExactlyOneArchitecturalRole() {
        return new ArchCondition<>("reside below exactly one reserved architectural role package") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                int roleCount = getRoleCount(javaClass);

                if (roleCount != 1) {
                    events.add(
                            SimpleConditionEvent.violated(
                                    javaClass,
                                    javaClass.getName()
                                            + " must reside below exactly one of "
                                            + ALLOWED_CONTEXT_PACKAGES
                                            + " after its bounded-context package"));
                }
            }

            private int getRoleCount(JavaClass javaClass) {
                String relativePackage =
                        javaClass.getPackageName().substring(ROOT_PACKAGE.length() + 1);

                String[] segments = relativePackage.split("\\.");
                int roleCount = 0;

                for (int index = 1; index < segments.length; index++) {
                    if (ALLOWED_CONTEXT_PACKAGES.contains(segments[index])) {
                        roleCount++;
                    }
                }
                return roleCount;
            }
        };
    }

    private static String[] allowedSubpackageTrees(
            String parentPackageTree, Set<String> allowedSubpackages) {
        return allowedSubpackages.stream()
                .map(subpackage -> subpackageTree(parentPackageTree, subpackage))
                .toArray(String[]::new);
    }

    private static String packageTree(String packagePath) {
        return CONTEXT_ROOT + ".." + packagePath + "..";
    }

    private static String subpackageTree(String parentPackageTree, String subpackage) {
        return parentPackageTree.substring(0, parentPackageTree.length() - 2)
                + "."
                + subpackage
                + "..";
    }
}
