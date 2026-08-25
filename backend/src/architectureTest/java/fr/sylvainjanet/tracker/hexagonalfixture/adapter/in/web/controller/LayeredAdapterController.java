package fr.sylvainjanet.tracker.hexagonalfixture.adapter.in.web.controller;

import fr.sylvainjanet.tracker.hexagonalfixture.application.service.LayeredApplicationService;

public final class LayeredAdapterController {

    private final LayeredApplicationService service;

    public LayeredAdapterController(LayeredApplicationService service) {
        this.service = service;
    }
}
