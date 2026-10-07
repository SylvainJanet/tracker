package fr.sylvainjanet.tracker.architecture.contract.tests;

import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterOutboundRules.PersistenceRules.PersistenceExceptionsRules.persistenceExceptionsExtendRuntimeException;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterOutboundRules.PersistenceRules.PersistenceExceptionsRules.persistenceExceptionsHaveExceptionSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterOutboundRules.PersistenceRules.PersistenceRepositoryRules.PersistenceRepositoryMapperRules.persistenceRepositoryMappersHaveMapperSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterOutboundRules.PersistenceRules.PersistenceRepositoryRules.PersistenceRepositoryMapperRules.persistenceRepositoryMappersShouldBeFinalClasses;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterOutboundRules.PersistenceRules.PersistenceRepositoryRules.classesNamedRepositoryStayInRepositoryPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterOutboundRules.PersistenceRules.PersistenceRepositoryRules.persistenceRepositoriesHaveRepositorySuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterOutboundRules.PersistenceRules.PersistenceRepositoryRules.persistenceRepositoriesShouldBeFinalClasses;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterOutboundRules.PersistenceRules.PersistenceRepositoryRules.persistenceRepositoriesUseAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterOutboundRules.PersistenceRules.persistenceAdaptersUseAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterOutboundRules.outboundAdaptersUseAllowedPackages;

import fr.sylvainjanet.tracker.architecture.contract.ArchitectureRuleContract;
import fr.sylvainjanet.tracker.architecturefixture.adapter.out.persistence.exceptions.CorrectlyNamedException;
import fr.sylvainjanet.tracker.architecturefixture.adapter.out.persistence.exceptions.MisconfiguredPersistenceFailure;
import fr.sylvainjanet.tracker.architecturefixture.adapter.out.persistence.repository.CorrectlyNamedRepository;
import fr.sylvainjanet.tracker.architecturefixture.adapter.out.persistence.repository.MisconfiguredPersistenceComponent;
import fr.sylvainjanet.tracker.architecturefixture.adapter.out.persistence.repository.mapper.CorrectlyNamedRepositoryMapper;
import fr.sylvainjanet.tracker.architecturefixture.adapter.out.persistence.repository.mapper.InvalidRepositoryMapperFunction;
import fr.sylvainjanet.tracker.architecturefixture.adapter.out.persistence.repository.unsupported.UnsupportedRepositoryType;
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
    void persistenceRepositoriesUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                persistenceRepositoriesUseAllowedPackages, CorrectlyNamedRepositoryMapper.class);
        ArchitectureRuleContract.assertRejects(
                persistenceRepositoriesUseAllowedPackages,
                "UnsupportedRepositoryType",
                UnsupportedRepositoryType.class);
    }

    @Test
    void persistenceRepositoryMappersHaveMapperSuffix() {
        ArchitectureRuleContract.assertAccepts(
                persistenceRepositoryMappersHaveMapperSuffix, CorrectlyNamedRepositoryMapper.class);
        ArchitectureRuleContract.assertRejects(
                persistenceRepositoryMappersHaveMapperSuffix,
                "InvalidRepositoryMapperFunction",
                InvalidRepositoryMapperFunction.class);
    }

    @Test
    void persistenceRepositoryMappersShouldBeFinalClasses() {
        ArchitectureRuleContract.assertAccepts(
                persistenceRepositoryMappersShouldBeFinalClasses,
                CorrectlyNamedRepositoryMapper.class);
        ArchitectureRuleContract.assertRejects(
                persistenceRepositoryMappersShouldBeFinalClasses,
                "InvalidRepositoryMapperFunction",
                InvalidRepositoryMapperFunction.class);
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
