package fr.sylvainjanet.tracker.hexagonalfixture.application.service;

import fr.sylvainjanet.tracker.hexagonalfixture.adapter.in.web.controller.LayeredAdapterController;

public final class InvalidApplicationDependencyService {

    private final LayeredAdapterController adapter;

    public InvalidApplicationDependencyService(LayeredAdapterController adapter) {
        this.adapter = adapter;
    }
}
