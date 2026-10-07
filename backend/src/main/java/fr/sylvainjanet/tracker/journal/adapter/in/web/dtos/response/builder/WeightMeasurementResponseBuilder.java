package fr.sylvainjanet.tracker.journal.adapter.in.web.dtos.response.builder;

import fr.sylvainjanet.tracker.journal.adapter.in.web.dtos.response.WeightMeasurementResponse;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class WeightMeasurementResponseBuilder {

    private LocalDate date;
    private BigDecimal weightInKg;

    private WeightMeasurementResponseBuilder() {}

    public static WeightMeasurementResponseBuilder aWeightMeasurementResponse() {
        return new WeightMeasurementResponseBuilder();
    }

    public WeightMeasurementResponseBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public WeightMeasurementResponseBuilder withWeightInKg(BigDecimal weightInKg) {
        this.weightInKg = weightInKg;
        return this;
    }

    public WeightMeasurementResponse build() {
        return new WeightMeasurementResponse(date, weightInKg);
    }
}
