package fr.sylvainjanet.tracker.hexagonalfixture.adapter.in.web.controller;

import fr.sylvainjanet.tracker.architecturefixture.application.service.CorrectlyNamedService;

public final class InboundBypassingController {

    private final CorrectlyNamedService service;

    public InboundBypassingController(CorrectlyNamedService service) {
        this.service = service;
    }
}
