package fr.sylvainjanet.tracker.analysis.adapter.in.web.controller.mapper;

import static fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder.AnalysisDateRangeResponseBuilder.anAnalysisDateRangeResponse;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisDateRangeResponse;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisDateRangeResult;

public final class AnalysisDateRangeResponseMapper {

    private AnalysisDateRangeResponseMapper() {}

    public static AnalysisDateRangeResponse dateRange(AnalysisDateRangeResult dateRange) {
        return anAnalysisDateRangeResponse()
                .withStartDate(dateRange.startDate())
                .withEndDate(dateRange.endDate())
                .build();
    }
}
