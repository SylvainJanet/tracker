package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisDateRangeResponse;
import java.time.LocalDate;

public final class AnalysisDateRangeResponseBuilder {

    private LocalDate startDate;
    private LocalDate endDate;

    private AnalysisDateRangeResponseBuilder() {}

    public static AnalysisDateRangeResponseBuilder anAnalysisDateRangeResponse() {
        return new AnalysisDateRangeResponseBuilder();
    }

    public AnalysisDateRangeResponseBuilder withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public AnalysisDateRangeResponseBuilder withEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public AnalysisDateRangeResponse build() {
        return new AnalysisDateRangeResponse(startDate, endDate);
    }
}
