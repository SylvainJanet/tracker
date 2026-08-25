package fr.sylvainjanet.tracker.hexagonalfixture.configuration;

import fr.sylvainjanet.tracker.hexagonalfixture.adapter.in.web.controller.LayeredAdapterController;

public final class LayeredConfiguration {

    private final LayeredAdapterController adapter;

    public LayeredConfiguration(LayeredAdapterController adapter) {
        this.adapter = adapter;
    }
}
