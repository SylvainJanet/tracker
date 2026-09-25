package fr.sylvainjanet.tracker.architecture.contract.tests;

import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.ServiceRules.ServiceMapperRules.serviceMappersHaveMapperSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.ServiceRules.ServiceMapperRules.serviceMappersShouldBeFinalClasses;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.ServiceRules.classesNamedServiceStayInServicePackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.ServiceRules.servicesHaveServiceSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.ServiceRules.servicesShouldBeFinal;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.ServiceRules.servicesUseAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ConfigurationRules.applicationConfigurationUsesAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ConfigurationRules.configurationClassesHaveAllowedSuffixes;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ConfigurationRules.configurationClassesStayInConfigurationPackage;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.contextClassesUseExactlyOneArchitecturalRole;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.fieldInjectionIsForbidden;

import fr.sylvainjanet.tracker.architecture.contract.ArchitectureRuleContract;
import fr.sylvainjanet.tracker.architecturefixture.application.service.CorrectlyNamedService;
import fr.sylvainjanet.tracker.architecturefixture.application.service.MisnamedServiceComponent;
import fr.sylvainjanet.tracker.architecturefixture.application.service.NonFinalService;
import fr.sylvainjanet.tracker.architecturefixture.application.service.mapper.InvalidServiceMapperFunction;
import fr.sylvainjanet.tracker.architecturefixture.application.service.mapper.ValidServiceMapper;
import fr.sylvainjanet.tracker.architecturefixture.application.service.unsupported.UnsupportedServicePackage;
import fr.sylvainjanet.tracker.architecturefixture.domain.ConstructorInjectedType;
import fr.sylvainjanet.tracker.architecturefixture.domain.CorrectlyLocatedDomainType;
import fr.sylvainjanet.tracker.architecturefixture.domain.FieldInjectedType;
import fr.sylvainjanet.tracker.architecturefixture.domain.MisplacedConfiguration;
import fr.sylvainjanet.tracker.architecturefixture.domain.MisplacedService;
import fr.sylvainjanet.tracker.architecturefixture.domain.application.MultipleRoleType;
import fr.sylvainjanet.tracker.architecturefixture.unclassified.UnclassifiedType;
import fr.sylvainjanet.tracker.configuration.sqlite.AnnotatedFixtureConfiguration;
import fr.sylvainjanet.tracker.configuration.sqlite.MisnamedConfigurationComponent;
import fr.sylvainjanet.tracker.configuration.sqlite.SqliteFixtureConfiguration;
import fr.sylvainjanet.tracker.configuration.sqlite.SqliteFixturePreparer;
import fr.sylvainjanet.tracker.configuration.unsupported.UnsupportedFixtureConfiguration;
import org.junit.jupiter.api.Test;

public class StructuralConventionContractTest {

    @Test
    void contextClassesUseExactlyOneArchitecturalRole() {
        ArchitectureRuleContract.assertAccepts(
                contextClassesUseExactlyOneArchitecturalRole, CorrectlyLocatedDomainType.class);

        ArchitectureRuleContract.assertRejects(
                contextClassesUseExactlyOneArchitecturalRole,
                "UnclassifiedType",
                UnclassifiedType.class);
        ArchitectureRuleContract.assertRejects(
                contextClassesUseExactlyOneArchitecturalRole,
                "MultipleRoleType",
                MultipleRoleType.class);
    }

    @Test
    void fieldInjectionIsForbidden() {
        ArchitectureRuleContract.assertAccepts(
                fieldInjectionIsForbidden, ConstructorInjectedType.class);

        ArchitectureRuleContract.assertRejects(
                fieldInjectionIsForbidden, "FieldInjectedType", FieldInjectedType.class);
    }

    @Test
    void servicesUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                servicesUseAllowedPackages, ValidServiceMapper.class);

        ArchitectureRuleContract.assertRejects(
                servicesUseAllowedPackages,
                "UnsupportedServicePackage",
                UnsupportedServicePackage.class);
    }

    @Test
    void servicesHaveServiceSuffix() {
        ArchitectureRuleContract.assertAccepts(
                servicesHaveServiceSuffix, CorrectlyNamedService.class);

        ArchitectureRuleContract.assertRejects(
                servicesHaveServiceSuffix,
                "MisnamedServiceComponent",
                MisnamedServiceComponent.class);
    }

    @Test
    void classesNamedServiceStayInServicePackages() {
        ArchitectureRuleContract.assertAccepts(
                classesNamedServiceStayInServicePackages, CorrectlyNamedService.class);

        ArchitectureRuleContract.assertRejects(
                classesNamedServiceStayInServicePackages,
                "MisplacedService",
                MisplacedService.class);
    }

    @Test
    void servicesShouldBeFinal() {
        ArchitectureRuleContract.assertAccepts(servicesShouldBeFinal, CorrectlyNamedService.class);

        ArchitectureRuleContract.assertRejects(
                servicesShouldBeFinal, "NonFinalService", NonFinalService.class);
    }

    @Test
    void serviceMappersHaveMapperSuffix() {
        ArchitectureRuleContract.assertAccepts(
                serviceMappersHaveMapperSuffix, ValidServiceMapper.class);

        ArchitectureRuleContract.assertRejects(
                serviceMappersHaveMapperSuffix,
                "InvalidServiceMapperFunction",
                InvalidServiceMapperFunction.class);
    }

    @Test
    void serviceMappersShouldBeFinalClasses() {
        ArchitectureRuleContract.assertAccepts(
                serviceMappersShouldBeFinalClasses, ValidServiceMapper.class);

        ArchitectureRuleContract.assertRejects(
                serviceMappersShouldBeFinalClasses,
                "InvalidServiceMapperFunction",
                InvalidServiceMapperFunction.class);
    }

    @Test
    void applicationConfigurationUsesAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                applicationConfigurationUsesAllowedPackages, SqliteFixtureConfiguration.class);

        ArchitectureRuleContract.assertRejects(
                applicationConfigurationUsesAllowedPackages,
                "UnsupportedFixtureConfiguration",
                UnsupportedFixtureConfiguration.class);
    }

    @Test
    void configurationClassesHaveAllowedSuffixes() {
        ArchitectureRuleContract.assertAccepts(
                configurationClassesHaveAllowedSuffixes,
                SqliteFixtureConfiguration.class,
                SqliteFixturePreparer.class);

        ArchitectureRuleContract.assertRejects(
                configurationClassesHaveAllowedSuffixes,
                "MisnamedConfigurationComponent",
                MisnamedConfigurationComponent.class);
    }

    @Test
    void configurationClassesStayInConfigurationPackage() {
        ArchitectureRuleContract.assertAccepts(
                configurationClassesStayInConfigurationPackage,
                AnnotatedFixtureConfiguration.class);

        ArchitectureRuleContract.assertRejects(
                configurationClassesStayInConfigurationPackage,
                "MisplacedConfiguration",
                MisplacedConfiguration.class);
    }
}
