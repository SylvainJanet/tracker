package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.AnalysisValueResponse;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class AnalysisValueResponseBuilder {

    private LocalDate date;
    private long dayNumber;
    private BigDecimal weightInKg;

    private AnalysisValueResponseBuilder() {}

    public static AnalysisValueResponseBuilder anAnalysisValueResponse() {
        return new AnalysisValueResponseBuilder();
    }

    public AnalysisValueResponseBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public AnalysisValueResponseBuilder withDayNumber(long dayNumber) {
        this.dayNumber = dayNumber;
        return this;
    }

    public AnalysisValueResponseBuilder withWeightInKg(BigDecimal weightInKg) {
        this.weightInKg = weightInKg;
        return this;
    }

    public AnalysisValueResponse build() {
        return new AnalysisValueResponse(date, dayNumber, weightInKg);
    }
}
