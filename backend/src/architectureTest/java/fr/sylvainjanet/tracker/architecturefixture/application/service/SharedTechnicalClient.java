package fr.sylvainjanet.tracker.architecturefixture.application.service;

import fr.sylvainjanet.tracker.technical.utility.SharedTechnicalUtility;

public final class SharedTechnicalClient {

    private final SharedTechnicalUtility utility;

    public SharedTechnicalClient(SharedTechnicalUtility utility) {
        this.utility = utility;
    }
}
