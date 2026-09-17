package fr.sylvainjanet.tracker.journal.domain;

import java.util.Objects;

public final class Weight {

    private final Float kilograms;

    private Weight(Float kilograms) {
        if (!Float.isFinite(kilograms) || kilograms <= 0.0f) {
            throw new IllegalArgumentException(
                    "weight must be a finite positive number of kilograms");
        }
        this.kilograms = kilograms;
    }

    public static Weight of(float kilograms) {
        return new Weight(kilograms);
    }

    public Float kilograms() {
        return kilograms;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Weight weight = (Weight) o;
        return Objects.equals(kilograms, weight.kilograms);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(kilograms);
    }
}
