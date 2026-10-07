package fr.sylvainjanet.tracker.journal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.journal.domain.entity.WeightMeasurement;
import fr.sylvainjanet.tracker.journal.domain.value.Weight;
import fr.sylvainjanet.tracker.shared.domain.DateIdentifier;
import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;

class WeightMeasurementTest {

    private static final LocalDate DATE = LocalDate.of(2026, Month.AUGUST, 25);
    private static final Weight WEIGHT = Weight.create(new BigDecimal("75.50"));

    @Test
    void createsAWeightMeasurement() {
        WeightMeasurement measurement = WeightMeasurement.create(DATE, WEIGHT);

        assertThat(measurement.date()).isEqualTo(DATE);
        assertThat(measurement.weight()).isEqualTo(WEIGHT);
        assertThat(measurement.weightInKilograms()).isEqualTo(new BigDecimal("75.50"));
        assertThat(measurement.identifier()).isEqualTo(DateIdentifier.create(DATE));
    }

    @Test
    void rejectsMissingDateAndWeightTogether() {
        assertThatThrownBy(() -> WeightMeasurement.create(null, null))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("date must not be null")
                .hasMessageContaining("weight must not be null");
    }

    @Test
    void usesTheDateAsItsIdentity() {
        WeightMeasurement first = WeightMeasurement.create(DATE, WEIGHT);
        WeightMeasurement sameDateDifferentWeight =
                WeightMeasurement.create(DATE, Weight.create(new BigDecimal("80.00")));
        WeightMeasurement differentDate = WeightMeasurement.create(DATE.plusDays(1), WEIGHT);

        assertThat(first).isEqualTo(sameDateDifferentWeight);
        assertThat(first.hashCode()).isEqualTo(sameDateDifferentWeight.hashCode());
        assertThat(first).isNotEqualTo(differentDate).isNotNull();
    }
}
