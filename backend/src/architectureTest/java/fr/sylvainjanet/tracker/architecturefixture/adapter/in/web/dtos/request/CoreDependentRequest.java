package fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.request;

import fr.sylvainjanet.tracker.architecturefixture.domain.CorrectlyLocatedDomainType;

public record CoreDependentRequest(CorrectlyLocatedDomainType value) {}
