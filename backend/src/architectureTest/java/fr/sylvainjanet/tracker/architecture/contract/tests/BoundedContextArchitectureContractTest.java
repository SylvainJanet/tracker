package fr.sylvainjanet.tracker.architecture.contract.tests;

import static fr.sylvainjanet.tracker.architecture.tests.BoundedContextArchitectureTest.contextsOnlyAccessOtherContextsThroughInboundContracts;
import static fr.sylvainjanet.tracker.architecture.tests.BoundedContextArchitectureTest.externallyUsedInboundContractsDoNotExposeContextInternals;

import fr.sylvainjanet.tracker.architecture.contract.ArchitectureRuleContract;
import fr.sylvainjanet.tracker.architecturefixture.application.service.InboundContractClient;
import fr.sylvainjanet.tracker.architecturefixture.application.service.InternalAccessClient;
import fr.sylvainjanet.tracker.architecturefixture.application.service.LeakingContractClient;
import fr.sylvainjanet.tracker.architecturefixture.application.service.SharedTechnicalClient;
import fr.sylvainjanet.tracker.otherarchitecturefixture.application.port.in.dtos.command.LeakingCommand;
import fr.sylvainjanet.tracker.otherarchitecturefixture.application.port.in.dtos.command.PublishedCommand;
import fr.sylvainjanet.tracker.otherarchitecturefixture.application.port.in.usecase.LeakingUseCase;
import fr.sylvainjanet.tracker.otherarchitecturefixture.application.port.in.usecase.PublishedUseCase;
import fr.sylvainjanet.tracker.otherarchitecturefixture.domain.InternalDomain;
import fr.sylvainjanet.tracker.technical.utility.SharedTechnicalUtility;
import org.junit.jupiter.api.Test;

public class BoundedContextArchitectureContractTest {

    @Test
    void contextsOnlyAccessOtherContextsThroughInboundContracts() {
        ArchitectureRuleContract.assertAccepts(
                contextsOnlyAccessOtherContextsThroughInboundContracts,
                InboundContractClient.class,
                PublishedUseCase.class,
                PublishedCommand.class,
                SharedTechnicalClient.class,
                SharedTechnicalUtility.class);
        ArchitectureRuleContract.assertRejects(
                contextsOnlyAccessOtherContextsThroughInboundContracts,
                "InternalDomain",
                InternalAccessClient.class,
                InternalDomain.class);
    }

    @Test
    void externallyUsedInboundContractsDoNotExposeContextInternals() {
        ArchitectureRuleContract.assertAccepts(
                externallyUsedInboundContractsDoNotExposeContextInternals,
                InboundContractClient.class,
                PublishedUseCase.class,
                PublishedCommand.class);
        ArchitectureRuleContract.assertRejects(
                externallyUsedInboundContractsDoNotExposeContextInternals,
                "InternalDomain",
                LeakingContractClient.class,
                LeakingUseCase.class,
                LeakingCommand.class,
                InternalDomain.class);
    }
}
