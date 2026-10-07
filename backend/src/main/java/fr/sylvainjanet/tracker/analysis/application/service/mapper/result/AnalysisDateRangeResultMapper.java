package fr.sylvainjanet.tracker.analysis.application.service.mapper.result;

import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.AnalysisDateRangeResultBuilder.anAnalysisDateRangeResult;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisDateRangeResult;
import fr.sylvainjanet.tracker.shared.domain.DateRange;

public final class AnalysisDateRangeResultMapper {

    private AnalysisDateRangeResultMapper() {}

    public static AnalysisDateRangeResult dateRange(DateRange domainDateRange) {
        return anAnalysisDateRangeResult()
                .withStartDate(domainDateRange.startDate())
                .withEndDate(domainDateRange.endDate())
                .build();
    }
}
