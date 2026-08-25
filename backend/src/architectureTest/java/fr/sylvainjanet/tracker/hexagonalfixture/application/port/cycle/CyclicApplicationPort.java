package fr.sylvainjanet.tracker.hexagonalfixture.application.port.cycle;

import fr.sylvainjanet.tracker.hexagonalfixture.application.service.CyclicApplicationService;

public final class CyclicApplicationPort {

    private final CyclicApplicationService service;

    public CyclicApplicationPort(CyclicApplicationService service) {
        this.service = service;
    }
}
