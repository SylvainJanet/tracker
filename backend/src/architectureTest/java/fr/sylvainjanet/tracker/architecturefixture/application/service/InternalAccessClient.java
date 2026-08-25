package fr.sylvainjanet.tracker.architecturefixture.application.service;

import fr.sylvainjanet.tracker.otherarchitecturefixture.domain.InternalDomain;

public final class InternalAccessClient {

    private final InternalDomain internalDomain;

    public InternalAccessClient(InternalDomain internalDomain) {
        this.internalDomain = internalDomain;
    }
}
