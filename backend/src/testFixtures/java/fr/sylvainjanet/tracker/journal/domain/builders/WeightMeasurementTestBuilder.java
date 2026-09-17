package fr.sylvainjanet.tracker.journal.domain.builders;

import fr.sylvainjanet.tracker.journal.domain.Weight;
import fr.sylvainjanet.tracker.journal.domain.WeightMeasurement;
import java.time.LocalDate;

public final class WeightMeasurementTestBuilder {
    private LocalDate date;
    private Weight weight;

    public static WeightMeasurementTestBuilder aWeightMeasurement() {
        return new WeightMeasurementTestBuilder();
    }

    public WeightMeasurementTestBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public WeightMeasurementTestBuilder withWeightInKg(Float weightInKg) {
        this.weight = Weight.of(weightInKg);
        return this;
    }

    public WeightMeasurement build() {
        return WeightMeasurement.create(date, weight);
    }
}
