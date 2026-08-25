package fr.sylvainjanet.tracker.hexagonalfixture.application.service;

import fr.sylvainjanet.tracker.hexagonalfixture.application.port.cycle.CyclicApplicationPort;

public final class CyclicApplicationService {

    private final CyclicApplicationPort port;

    public CyclicApplicationService(CyclicApplicationPort port) {
        this.port = port;
    }
}
