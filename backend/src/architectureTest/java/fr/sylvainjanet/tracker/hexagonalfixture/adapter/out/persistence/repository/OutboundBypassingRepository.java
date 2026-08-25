package fr.sylvainjanet.tracker.hexagonalfixture.adapter.out.persistence.repository;

import fr.sylvainjanet.tracker.architecturefixture.application.port.in.usecase.CorrectlyNamedUseCase;

public final class OutboundBypassingRepository {

    private final CorrectlyNamedUseCase useCase;

    public OutboundBypassingRepository(CorrectlyNamedUseCase useCase) {
        this.useCase = useCase;
    }
}
