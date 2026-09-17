package fr.sylvainjanet.tracker.tracking.domain;

import java.time.LocalDate;
import java.util.Objects;

public final class DailyRecord {

    private final LocalDate date;
    private final Weight weight;

    private DailyRecord(LocalDate date, Weight weight) {
        this.date = Objects.requireNonNull(date, "date must not be null");
        this.weight = Objects.requireNonNull(weight, "weight must not be null");
    }

    public static DailyRecord create(LocalDate date, Weight weight) {
        return new DailyRecord(date, weight);
    }

    public LocalDate date() {
        return date;
    }

    public Weight weight() {
        return weight;
    }

    public Float weightInKilograms() {
        return weight.kilograms();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return other instanceof DailyRecord that && date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }
}
