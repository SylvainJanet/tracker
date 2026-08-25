package fr.sylvainjanet.tracker.hexagonalfixture.adapter.in.web.controller;

import fr.sylvainjanet.tracker.architecturefixture.application.port.in.usecase.CorrectlyNamedUseCase;

public final class InboundUseCaseController {

    private final CorrectlyNamedUseCase useCase;

    public InboundUseCaseController(CorrectlyNamedUseCase useCase) {
        this.useCase = useCase;
    }
}
