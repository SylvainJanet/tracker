package fr.sylvainjanet.tracker.journal.application.port.in.dtos.builders.result;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.LogWeightMeasurementResult;
import java.math.BigDecimal;
import java.time.LocalDate;

public class LogWeightMeasurementResultTestBuilder {

    private LocalDate date;
    private BigDecimal weightInKg;

    public static LogWeightMeasurementResultTestBuilder aLogWeightMeasurementResult() {
        return new LogWeightMeasurementResultTestBuilder();
    }

    public LogWeightMeasurementResultTestBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public LogWeightMeasurementResultTestBuilder withWeightInKg(BigDecimal weightInKg) {
        this.weightInKg = weightInKg;
        return this;
    }

    public LogWeightMeasurementResult build() {
        return new LogWeightMeasurementResult(date, weightInKg);
    }
}
