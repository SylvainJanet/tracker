package fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.response.mapper;

import fr.sylvainjanet.tracker.architecturefixture.domain.CorrectlyLocatedDomainType;

public final class DomainDependentResponseMapper {

    private final CorrectlyLocatedDomainType domainType;

    public DomainDependentResponseMapper(CorrectlyLocatedDomainType domainType) {
        this.domainType = domainType;
    }
}
