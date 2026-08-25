package fr.sylvainjanet.tracker.tracking.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;

class DailyRecordTest {

    @Test
    void newRecordHasTheProvidedDate() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);

        DailyRecord dailyRecord = DailyRecord.create(date);

        assertThat(dailyRecord.date()).isEqualTo(date);
    }

    @Test
    void newRecordStartsInProgress() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);

        DailyRecord dailyRecord = DailyRecord.create(date);

        assertThat(dailyRecord.status()).isEqualTo(CompletionStatus.IN_PROGRESS);
    }

    @Test
    void recordCannotBeCreatedWithoutDate() {
        assertThatThrownBy(() -> DailyRecord.create(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("date must not be null");
    }

    @Test
    void reconstitutedRecordRetainsItsStatus() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);

        DailyRecord dailyRecord = DailyRecord.reconstitute(date, CompletionStatus.COMPLETED);

        assertThat(dailyRecord.date()).isEqualTo(date);
        assertThat(dailyRecord.status()).isEqualTo(CompletionStatus.COMPLETED);
    }

    @Test
    void recordCannotBeReconstitutedWithoutStatus() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);

        assertThatThrownBy(() -> DailyRecord.reconstitute(date, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("status must not be null");
    }

    @Test
    void recordsWithTheSameDateAreEqualRegardlessOfStatus() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
        DailyRecord inProgress = DailyRecord.reconstitute(date, CompletionStatus.IN_PROGRESS);
        DailyRecord completed = DailyRecord.reconstitute(date, CompletionStatus.COMPLETED);

        assertThat(inProgress).isEqualTo(completed);
    }

    @Test
    void recordsWithDifferentDatesAreNotEqual() {
        DailyRecord first = DailyRecord.create(LocalDate.of(2026, Month.AUGUST, 25));
        DailyRecord second = DailyRecord.create(LocalDate.of(2026, Month.AUGUST, 26));

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void equalRecordsHaveTheSameHashCode() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
        DailyRecord inProgress = DailyRecord.reconstitute(date, CompletionStatus.IN_PROGRESS);
        DailyRecord completed = DailyRecord.reconstitute(date, CompletionStatus.COMPLETED);

        assertThat(inProgress).hasSameHashCodeAs(completed);
    }
}
