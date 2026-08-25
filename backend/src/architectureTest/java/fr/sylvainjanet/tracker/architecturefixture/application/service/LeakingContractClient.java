package fr.sylvainjanet.tracker.architecturefixture.application.service;

import fr.sylvainjanet.tracker.otherarchitecturefixture.application.port.in.usecase.LeakingUseCase;

public final class LeakingContractClient {

    private final LeakingUseCase useCase;

    public LeakingContractClient(LeakingUseCase useCase) {
        this.useCase = useCase;
    }
}
