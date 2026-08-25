package fr.sylvainjanet.tracker.architecture.contract.tests;

import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterOutboundRules.PersistenceRules.PersistenceExceptionsRules.persistenceExceptionsExtendRuntimeException;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterOutboundRules.PersistenceRules.PersistenceExceptionsRules.persistenceExceptionsHaveExceptionSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterOutboundRules.PersistenceRules.PersistenceRepositoryRules.classesNamedRepositoryStayInRepositoryPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterOutboundRules.PersistenceRules.PersistenceRepositoryRules.persistenceRepositoriesHaveRepositorySuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterOutboundRules.PersistenceRules.PersistenceRepositoryRules.persistenceRepositoriesShouldBeFinalClasses;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterOutboundRules.PersistenceRules.persistenceAdaptersUseAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterOutboundRules.outboundAdaptersUseAllowedPackages;

import fr.sylvainjanet.tracker.architecture.contract.ArchitectureRuleContract;
import fr.sylvainjanet.tracker.architecturefixture.adapter.out.persistence.exceptions.CorrectlyNamedException;
import fr.sylvainjanet.tracker.architecturefixture.adapter.out.persistence.exceptions.MisconfiguredPersistenceFailure;
import fr.sylvainjanet.tracker.architecturefixture.adapter.out.persistence.repository.CorrectlyNamedRepository;
import fr.sylvainjanet.tracker.architecturefixture.adapter.out.persistence.repository.MisconfiguredPersistenceComponent;
import fr.sylvainjanet.tracker.architecturefixture.adapter.out.persistence.unsupported.UnsupportedPersistenceType;
import fr.sylvainjanet.tracker.architecturefixture.adapter.out.unsupported.UnsupportedOutboundAdapterType;
import fr.sylvainjanet.tracker.architecturefixture.domain.MisplacedRepository;
import org.junit.jupiter.api.Test;

public class StructuralPersistenceConventionContractTest {

    @Test
    void outboundAdaptersUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                outboundAdaptersUseAllowedPackages, CorrectlyNamedRepository.class);
        ArchitectureRuleContract.assertRejects(
                outboundAdaptersUseAllowedPackages,
                "UnsupportedOutboundAdapterType",
                UnsupportedOutboundAdapterType.class);
    }

    @Test
    void persistenceAdaptersUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                persistenceAdaptersUseAllowedPackages, CorrectlyNamedRepository.class);
        ArchitectureRuleContract.assertRejects(
                persistenceAdaptersUseAllowedPackages,
                "UnsupportedPersistenceType",
                UnsupportedPersistenceType.class);
    }

    @Test
    void persistenceExceptionsHaveExceptionSuffix() {
        ArchitectureRuleContract.assertAccepts(
                persistenceExceptionsHaveExceptionSuffix, CorrectlyNamedException.class);
        ArchitectureRuleContract.assertRejects(
                persistenceExceptionsHaveExceptionSuffix,
                "MisconfiguredPersistenceFailure",
                MisconfiguredPersistenceFailure.class);
    }

    @Test
    void persistenceExceptionsExtendRuntimeException() {
        ArchitectureRuleContract.assertAccepts(
                persistenceExceptionsExtendRuntimeException, CorrectlyNamedException.class);
        ArchitectureRuleContract.assertRejects(
                persistenceExceptionsExtendRuntimeException,
                "MisconfiguredPersistenceFailure",
                MisconfiguredPersistenceFailure.class);
    }

    @Test
    void persistenceRepositoriesHaveRepositorySuffix() {
        ArchitectureRuleContract.assertAccepts(
                persistenceRepositoriesHaveRepositorySuffix, CorrectlyNamedRepository.class);
        ArchitectureRuleContract.assertRejects(
                persistenceRepositoriesHaveRepositorySuffix,
                "MisconfiguredPersistenceComponent",
                MisconfiguredPersistenceComponent.class);
    }

    @Test
    void classesNamedRepositoryStayInRepositoryPackages() {
        ArchitectureRuleContract.assertAccepts(
                classesNamedRepositoryStayInRepositoryPackages, CorrectlyNamedRepository.class);
        ArchitectureRuleContract.assertRejects(
                classesNamedRepositoryStayInRepositoryPackages,
                "MisplacedRepository",
                MisplacedRepository.class);
    }

    @Test
    void persistenceRepositoriesShouldBeFinalClasses() {
        ArchitectureRuleContract.assertAccepts(
                persistenceRepositoriesShouldBeFinalClasses, CorrectlyNamedRepository.class);
        ArchitectureRuleContract.assertRejects(
                persistenceRepositoriesShouldBeFinalClasses,
                "MisconfiguredPersistenceComponent",
                MisconfiguredPersistenceComponent.class);
    }
}
