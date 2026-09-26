package fr.sylvainjanet.tracker.journal.domain.builder;

import fr.sylvainjanet.tracker.journal.domain.Weight;
import fr.sylvainjanet.tracker.journal.domain.WeightMeasurement;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class WeightMeasurementBuilder {
    private LocalDate date;
    private Weight weight;

    public static WeightMeasurementBuilder aWeightMeasurement() {
        return new WeightMeasurementBuilder();
    }

    public WeightMeasurementBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public WeightMeasurementBuilder withWeightInKg(BigDecimal weightInKg) {
        this.weight = Weight.of(weightInKg);
        return this;
    }

    public WeightMeasurement build() {
        return WeightMeasurement.create(date, weight);
    }
}
