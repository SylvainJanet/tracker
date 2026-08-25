package fr.sylvainjanet.tracker.hexagonalfixture.application.service;

import fr.sylvainjanet.tracker.architecturefixture.application.port.in.usecase.CorrectlyNamedUseCase;

public final class AcyclicApplicationService {

    private final CorrectlyNamedUseCase useCase;

    public AcyclicApplicationService(CorrectlyNamedUseCase useCase) {
        this.useCase = useCase;
    }
}
