package fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.response.mapper;

import fr.sylvainjanet.tracker.architecturefixture.application.port.in.dtos.result.CorrectlyNamedResult;

public final class ResultDependentResponseMapper {

    private final CorrectlyNamedResult result;

    public ResultDependentResponseMapper(CorrectlyNamedResult result) {
        this.result = result;
    }
}
