package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisDateRangeResult;
import java.time.LocalDate;

public final class AnalysisDateRangeResultBuilder {

    private LocalDate startDate;
    private LocalDate endDate;

    private AnalysisDateRangeResultBuilder() {}

    public static AnalysisDateRangeResultBuilder anAnalysisDateRangeResult() {
        return new AnalysisDateRangeResultBuilder();
    }

    public AnalysisDateRangeResultBuilder withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public AnalysisDateRangeResultBuilder withEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public AnalysisDateRangeResult build() {
        return new AnalysisDateRangeResult(startDate, endDate);
    }
}
