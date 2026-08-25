package fr.sylvainjanet.tracker.architecturefixture.application.service;

import fr.sylvainjanet.tracker.otherarchitecturefixture.application.port.in.usecase.PublishedUseCase;

public final class InboundContractClient {

    private final PublishedUseCase useCase;

    public InboundContractClient(PublishedUseCase useCase) {
        this.useCase = useCase;
    }
}
