package fr.sylvainjanet.tracker.architecture.contract.tests;

import static fr.sylvainjanet.tracker.architecture.tests.HexagonalArchitectureTest.applicationOnlyDependsOnCoreAndAllowedJdkPackages;
import static fr.sylvainjanet.tracker.architecture.tests.HexagonalArchitectureTest.applicationPackagesAreFreeOfCycles;
import static fr.sylvainjanet.tracker.architecture.tests.HexagonalArchitectureTest.applicationServicesImplementUseCases;
import static fr.sylvainjanet.tracker.architecture.tests.HexagonalArchitectureTest.domainOnlyDependsOnDomainAndAllowedJdkPackages;
import static fr.sylvainjanet.tracker.architecture.tests.HexagonalArchitectureTest.inboundAdaptersDoNotBypassInboundPorts;
import static fr.sylvainjanet.tracker.architecture.tests.HexagonalArchitectureTest.layersRespectHexagonalDependencies;
import static fr.sylvainjanet.tracker.architecture.tests.HexagonalArchitectureTest.outboundAdaptersDoNotUseInboundSide;
import static fr.sylvainjanet.tracker.architecture.tests.HexagonalArchitectureTest.outboundAdaptersImplementOutboundPorts;
import static fr.sylvainjanet.tracker.architecture.tests.HexagonalArchitectureTest.persistenceTechnologyStaysInPersistenceBoundary;
import static fr.sylvainjanet.tracker.architecture.tests.HexagonalArchitectureTest.webTechnologyStaysInWebAdapter;

import fr.sylvainjanet.tracker.architecture.contract.ArchitectureRuleContract;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.controller.CorrectlyNamedController;
import fr.sylvainjanet.tracker.architecturefixture.adapter.out.persistence.repository.CorrectlyNamedRepository;
import fr.sylvainjanet.tracker.architecturefixture.application.port.in.usecase.CorrectlyNamedUseCase;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.gateway.store.CorrectlyNamedStore;
import fr.sylvainjanet.tracker.architecturefixture.application.service.CorrectlyNamedService;
import fr.sylvainjanet.tracker.architecturefixture.domain.CorrectlyLocatedDomainType;
import fr.sylvainjanet.tracker.hexagonalfixture.adapter.in.web.controller.InboundBypassingController;
import fr.sylvainjanet.tracker.hexagonalfixture.adapter.in.web.controller.InboundUseCaseController;
import fr.sylvainjanet.tracker.hexagonalfixture.adapter.in.web.controller.LayeredAdapterController;
import fr.sylvainjanet.tracker.hexagonalfixture.adapter.out.persistence.repository.JdbcPersistenceRepository;
import fr.sylvainjanet.tracker.hexagonalfixture.adapter.out.persistence.repository.OutboundBypassingRepository;
import fr.sylvainjanet.tracker.hexagonalfixture.adapter.out.persistence.repository.OutboundPortRepository;
import fr.sylvainjanet.tracker.hexagonalfixture.application.port.cycle.CyclicApplicationPort;
import fr.sylvainjanet.tracker.hexagonalfixture.application.service.AcyclicApplicationService;
import fr.sylvainjanet.tracker.hexagonalfixture.application.service.CyclicApplicationService;
import fr.sylvainjanet.tracker.hexagonalfixture.application.service.InvalidApplicationDependencyService;
import fr.sylvainjanet.tracker.hexagonalfixture.application.service.LayeredApplicationService;
import fr.sylvainjanet.tracker.hexagonalfixture.application.service.UseCaseImplementingService;
import fr.sylvainjanet.tracker.hexagonalfixture.configuration.LayeredConfiguration;
import fr.sylvainjanet.tracker.hexagonalfixture.domain.InvalidDomainDependency;
import fr.sylvainjanet.tracker.hexagonalfixture.domain.LayeredDomain;
import fr.sylvainjanet.tracker.hexagonalfixture.domain.PersistenceTechnologyLeak;
import fr.sylvainjanet.tracker.hexagonalfixture.domain.WebTechnologyLeak;
import org.junit.jupiter.api.Test;

public class HexagonalArchitectureContractTest {

    @Test
    void layersRespectHexagonalDependencies() {
        ArchitectureRuleContract.assertAccepts(
                layersRespectHexagonalDependencies,
                LayeredDomain.class,
                LayeredApplicationService.class,
                LayeredAdapterController.class,
                LayeredConfiguration.class);
        ArchitectureRuleContract.assertRejects(
                layersRespectHexagonalDependencies,
                "InvalidDomainDependency",
                InvalidDomainDependency.class,
                LayeredApplicationService.class);
    }

    @Test
    void domainOnlyDependsOnDomainAndAllowedJdkPackages() {
        ArchitectureRuleContract.assertAccepts(
                domainOnlyDependsOnDomainAndAllowedJdkPackages, LayeredDomain.class);
        ArchitectureRuleContract.assertRejects(
                domainOnlyDependsOnDomainAndAllowedJdkPackages,
                "InvalidDomainDependency",
                InvalidDomainDependency.class,
                LayeredApplicationService.class);
    }

    @Test
    void applicationOnlyDependsOnCoreAndAllowedJdkPackages() {
        ArchitectureRuleContract.assertAccepts(
                applicationOnlyDependsOnCoreAndAllowedJdkPackages,
                LayeredApplicationService.class,
                LayeredDomain.class);
        ArchitectureRuleContract.assertRejects(
                applicationOnlyDependsOnCoreAndAllowedJdkPackages,
                "InvalidApplicationDependencyService",
                InvalidApplicationDependencyService.class,
                LayeredAdapterController.class);
    }

    @Test
    void webTechnologyStaysInWebAdapter() {
        ArchitectureRuleContract.assertAccepts(
                webTechnologyStaysInWebAdapter,
                CorrectlyLocatedDomainType.class,
                CorrectlyNamedController.class);
        ArchitectureRuleContract.assertRejects(
                webTechnologyStaysInWebAdapter, "WebTechnologyLeak", WebTechnologyLeak.class);
    }

    @Test
    void persistenceTechnologyStaysInPersistenceBoundary() {
        ArchitectureRuleContract.assertAccepts(
                persistenceTechnologyStaysInPersistenceBoundary,
                CorrectlyLocatedDomainType.class,
                JdbcPersistenceRepository.class);
        ArchitectureRuleContract.assertRejects(
                persistenceTechnologyStaysInPersistenceBoundary,
                "PersistenceTechnologyLeak",
                PersistenceTechnologyLeak.class);
    }

    @Test
    void inboundAdaptersDoNotBypassInboundPorts() {
        ArchitectureRuleContract.assertAccepts(
                inboundAdaptersDoNotBypassInboundPorts,
                InboundUseCaseController.class,
                CorrectlyNamedUseCase.class);
        ArchitectureRuleContract.assertRejects(
                inboundAdaptersDoNotBypassInboundPorts,
                "InboundBypassingController",
                InboundBypassingController.class,
                CorrectlyNamedService.class);
    }

    @Test
    void outboundAdaptersDoNotUseInboundSide() {
        ArchitectureRuleContract.assertAccepts(
                outboundAdaptersDoNotUseInboundSide,
                OutboundPortRepository.class,
                CorrectlyNamedStore.class);
        ArchitectureRuleContract.assertRejects(
                outboundAdaptersDoNotUseInboundSide,
                "OutboundBypassingRepository",
                OutboundBypassingRepository.class,
                CorrectlyNamedUseCase.class);
    }

    @Test
    void applicationServicesImplementUseCases() {
        ArchitectureRuleContract.assertAccepts(
                applicationServicesImplementUseCases,
                UseCaseImplementingService.class,
                CorrectlyNamedUseCase.class);
        ArchitectureRuleContract.assertRejects(
                applicationServicesImplementUseCases,
                "CorrectlyNamedService",
                CorrectlyNamedService.class);
    }

    @Test
    void outboundAdaptersImplementOutboundPorts() {
        ArchitectureRuleContract.assertAccepts(
                outboundAdaptersImplementOutboundPorts,
                OutboundPortRepository.class,
                CorrectlyNamedStore.class);
        ArchitectureRuleContract.assertRejects(
                outboundAdaptersImplementOutboundPorts,
                "CorrectlyNamedRepository",
                CorrectlyNamedRepository.class);
    }

    @Test
    void applicationPackagesAreFreeOfCycles() {
        ArchitectureRuleContract.assertAccepts(
                applicationPackagesAreFreeOfCycles,
                AcyclicApplicationService.class,
                CorrectlyNamedUseCase.class);
        ArchitectureRuleContract.assertRejects(
                applicationPackagesAreFreeOfCycles,
                "CyclicApplicationService",
                CyclicApplicationService.class,
                CyclicApplicationPort.class);
    }
}
