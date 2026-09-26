package fr.sylvainjanet.tracker.journal.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public final class WeightMeasurement {

    private final LocalDate date;
    private final Weight weight;

    private WeightMeasurement(LocalDate date, Weight weight) {
        this.date = Objects.requireNonNull(date, "date must not be null");
        this.weight = Objects.requireNonNull(weight, "weight must not be null");
    }

    public static WeightMeasurement create(LocalDate date, Weight weight) {
        return new WeightMeasurement(date, weight);
    }

    public LocalDate date() {
        return date;
    }

    public Weight weight() {
        return weight;
    }

    public BigDecimal weightInKilograms() {
        return weight.inKilograms();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WeightMeasurement that = (WeightMeasurement) o;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(date);
    }
}
