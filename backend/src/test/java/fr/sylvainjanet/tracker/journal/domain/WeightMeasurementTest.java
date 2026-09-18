package fr.sylvainjanet.tracker.journal.domain;

import static fr.sylvainjanet.tracker.journal.domain.builders.WeightMeasurementTestBuilder.aWeightMeasurement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;

class WeightMeasurementTest {

    @Test
    void createdLogWeightMeasurementHasTheProvidedDateAndWeight() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
        Weight weight = Weight.of(BigDecimal.valueOf(123.0f));

        WeightMeasurement weightMeasurement = WeightMeasurement.create(date, weight);

        assertThat(weightMeasurement.date()).isEqualTo(date);
        assertThat(weightMeasurement.weight()).isEqualTo(weight);
    }

    @Test
    void weightMeasurementCannotBeCreatedWithoutDate() {
        Weight weight = Weight.of(BigDecimal.valueOf(123.0f));
        assertThatThrownBy(() -> WeightMeasurement.create(null, weight))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("date must not be null");
    }

    @Test
    void weightMeasurementCannotBeCreatedWithoutWeight() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
        assertThatThrownBy(() -> WeightMeasurement.create(date, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("weight must not be null");
    }

    @Test
    void weightMeasurementsWithTheSameDateAreEqualRegardlessOfWeight() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
        BigDecimal firstWeight = BigDecimal.valueOf(123.0f);
        BigDecimal secondWeight = BigDecimal.valueOf(124.0f);

        WeightMeasurement first =
                aWeightMeasurement().withDate(date).withWeightInKg(firstWeight).build();
        WeightMeasurement second =
                aWeightMeasurement().withDate(date).withWeightInKg(secondWeight).build();

        assertThat(first).isEqualTo(second);
    }

    @Test
    void weightMeasurementsWithDifferentDatesAreNotEqual() {
        LocalDate firstDate = LocalDate.of(2026, Month.AUGUST, 25);
        LocalDate secondDate = LocalDate.of(2026, Month.AUGUST, 26);
        BigDecimal weight = BigDecimal.valueOf(123.0f);
        WeightMeasurement first =
                aWeightMeasurement().withDate(firstDate).withWeightInKg(weight).build();
        WeightMeasurement second =
                aWeightMeasurement().withDate(secondDate).withWeightInKg(weight).build();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void equalWeightMeasurementsHaveTheSameHashCode() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
        BigDecimal weight = BigDecimal.valueOf(123.0f);
        WeightMeasurement inProgress =
                aWeightMeasurement().withDate(date).withWeightInKg(weight).build();
        WeightMeasurement completed =
                aWeightMeasurement().withDate(date).withWeightInKg(weight).build();

        assertThat(inProgress).hasSameHashCodeAs(completed);
    }
}
