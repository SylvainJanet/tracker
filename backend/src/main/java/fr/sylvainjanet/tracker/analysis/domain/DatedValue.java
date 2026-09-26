package fr.sylvainjanet.tracker.analysis.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public record DatedValue(LocalDate date, BigDecimal value) {

    public DatedValue {
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(value, "value must not be null");
    }
}
