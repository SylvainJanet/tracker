package fr.sylvainjanet.tracker.analysis.adapter.in.web.controller.mapper;

import static fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisRoundingResponse.PRECISE;
import static fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisRoundingResponse.PRETTY;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisRoundingResponse;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRoundingResult;

public final class AnalysisRoundingResponseMapper {

    private AnalysisRoundingResponseMapper() {}

    public static AnalysisRoundingResponse rounding(AnalysisRoundingResult rounding) {

        return switch (rounding) {
            case PRECISE -> PRECISE;
            case PRETTY -> PRETTY;
        };
    }
}
