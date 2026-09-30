package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisValueResult;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class AnalysisValueResultBuilder {

    private LocalDate date;
    private long dayNumber;
    private BigDecimal weightInKg;

    private AnalysisValueResultBuilder() {}

    public static AnalysisValueResultBuilder anAnalysisValueResult() {
        return new AnalysisValueResultBuilder();
    }

    public AnalysisValueResultBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public AnalysisValueResultBuilder withDayNumber(long dayNumber) {
        this.dayNumber = dayNumber;
        return this;
    }

    public AnalysisValueResultBuilder withWeightInKg(BigDecimal weightInKg) {
        this.weightInKg = weightInKg;
        return this;
    }

    public AnalysisValueResult build() {
        return new AnalysisValueResult(date, dayNumber, weightInKg);
    }
}
