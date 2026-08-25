package fr.sylvainjanet.tracker.hexagonalfixture.domain;

import fr.sylvainjanet.tracker.hexagonalfixture.application.service.LayeredApplicationService;

public final class InvalidDomainDependency {

    private final LayeredApplicationService service;

    public InvalidDomainDependency(LayeredApplicationService service) {
        this.service = service;
    }
}
