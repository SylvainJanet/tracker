package fr.sylvainjanet.tracker.hexagonalfixture.application.service;

import fr.sylvainjanet.tracker.hexagonalfixture.domain.LayeredDomain;
import java.util.List;

public final class LayeredApplicationService {

    private final LayeredDomain domain;

    public LayeredApplicationService(LayeredDomain domain) {
        this.domain = domain;
    }

    public List<String> copyValues(List<String> values) {
        return values.stream().toList();
    }
}
