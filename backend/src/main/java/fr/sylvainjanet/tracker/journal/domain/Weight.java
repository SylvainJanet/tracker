package fr.sylvainjanet.tracker.journal.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Weight {

    private static final long GRAMS_MEASURABLE_UNIT = 50L;

    private final long grams;

    private Weight(long grams) {
        if (grams <= 0) {
            throw new IllegalArgumentException("weight must be a positive number of grams");
        }

        if (grams % GRAMS_MEASURABLE_UNIT != 0) {
            throw new IllegalArgumentException(
                    "weight must be a multiple of " + GRAMS_MEASURABLE_UNIT + " grams");
        }
        this.grams = grams;
    }

    public static Weight of(BigDecimal kilograms) {
        long grams = kilograms.multiply(BigDecimal.valueOf(1000)).longValueExact();
        return new Weight(grams);
    }

    public BigDecimal inKilograms() {
        return BigDecimal.valueOf(grams, 3).setScale(2, RoundingMode.UNNECESSARY);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Weight weight = (Weight) o;
        return Objects.equals(grams, weight.grams);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(grams);
    }
}
