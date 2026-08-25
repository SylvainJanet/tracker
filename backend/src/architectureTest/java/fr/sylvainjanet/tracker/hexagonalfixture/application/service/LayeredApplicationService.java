package fr.sylvainjanet.tracker.hexagonalfixture.application.service;

import fr.sylvainjanet.tracker.hexagonalfixture.domain.LayeredDomain;

public final class LayeredApplicationService {

    private final LayeredDomain domain;

    public LayeredApplicationService(LayeredDomain domain) {
        this.domain = domain;
    }
}
