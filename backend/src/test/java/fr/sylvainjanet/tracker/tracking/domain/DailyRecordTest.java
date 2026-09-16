package fr.sylvainjanet.tracker.tracking.domain;

import static fr.sylvainjanet.tracker.tracking.domain.builders.DailyRecordTestBuilder.aDailyRecord;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;

class DailyRecordTest {

    @Test
    void newRecordHasTheProvidedDateAndWeight() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
        Weight weight = Weight.of(123.0f);

        DailyRecord dailyRecord = DailyRecord.create(date, weight);

        assertThat(dailyRecord.date()).isEqualTo(date);
        assertThat(dailyRecord.weight()).isEqualTo(weight);
    }

    @Test
    void recordCannotBeCreatedWithoutDate() {
        Weight weight = Weight.of(123.0f);
        assertThatThrownBy(() -> DailyRecord.create(null, weight))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("date must not be null");
    }

    @Test
    void recordCannotBeCreatedWithoutWeight() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
        assertThatThrownBy(() -> DailyRecord.create(date, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("weight must not be null");
    }

    @Test
    void recordsWithTheSameDateAreEqualRegardlessOfWeight() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
        Float firstWeight = 123.0f;
        Float secondWeight = 124.0f;

        DailyRecord first = aDailyRecord().withDate(date).withWeightInKg(firstWeight).build();
        DailyRecord second = aDailyRecord().withDate(date).withWeightInKg(secondWeight).build();

        assertThat(first).isEqualTo(second);
    }

    @Test
    void recordsWithDifferentDatesAreNotEqual() {
        LocalDate firstDate = LocalDate.of(2026, Month.AUGUST, 25);
        LocalDate secondDate = LocalDate.of(2026, Month.AUGUST, 26);
        Float weight = 123.0f;
        DailyRecord first = aDailyRecord().withDate(firstDate).withWeightInKg(weight).build();
        DailyRecord second = aDailyRecord().withDate(secondDate).withWeightInKg(weight).build();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void equalRecordsHaveTheSameHashCode() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
        Float weight = 123.0f;
        DailyRecord inProgress = aDailyRecord().withDate(date).withWeightInKg(weight).build();
        DailyRecord completed = aDailyRecord().withDate(date).withWeightInKg(weight).build();

        assertThat(inProgress).hasSameHashCodeAs(completed);
    }
}
