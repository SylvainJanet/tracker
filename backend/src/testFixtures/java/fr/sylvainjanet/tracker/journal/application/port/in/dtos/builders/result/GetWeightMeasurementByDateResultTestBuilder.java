package fr.sylvainjanet.tracker.journal.application.port.in.dtos.builders.result;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementByDateResult;
import java.math.BigDecimal;
import java.time.LocalDate;

public class GetWeightMeasurementByDateResultTestBuilder {
    private LocalDate date;
    private BigDecimal weightInKg;

    public static GetWeightMeasurementByDateResultTestBuilder aGetWeightMeasurementByDateResult() {
        return new GetWeightMeasurementByDateResultTestBuilder();
    }

    public GetWeightMeasurementByDateResultTestBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public GetWeightMeasurementByDateResultTestBuilder withWeightInKg(BigDecimal weightInKg) {
        this.weightInKg = weightInKg;
        return this;
    }

    public GetWeightMeasurementByDateResult build() {
        return new GetWeightMeasurementByDateResult(date, weightInKg);
    }
}
