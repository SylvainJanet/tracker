package fr.sylvainjanet.tracker.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;

class DateRangeTest {

    @Test
    void createsAnInclusiveDateRange() {
        LocalDate startDate = LocalDate.of(2026, Month.SEPTEMBER, 1);
        LocalDate endDate = LocalDate.of(2026, Month.SEPTEMBER, 6);

        DateRange dateRange = DateRange.of(startDate, endDate);

        assertThat(dateRange.getStartDate()).isEqualTo(startDate);
        assertThat(dateRange.getEndDate()).isEqualTo(endDate);
    }

    @Test
    void acceptsTheSameDateAsBothBoundaries() {
        LocalDate date = LocalDate.of(2026, Month.SEPTEMBER, 1);

        assertThatCode(() -> DateRange.of(date, date)).doesNotThrowAnyException();
    }

    @Test
    void rejectsANullStartDate() {
        LocalDate endDate = LocalDate.of(2026, Month.SEPTEMBER, 6);

        assertThatThrownBy(() -> DateRange.of(null, endDate))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("start date must not be null");
    }

    @Test
    void rejectsANullEndDate() {
        LocalDate startDate = LocalDate.of(2026, Month.SEPTEMBER, 1);

        assertThatThrownBy(() -> DateRange.of(startDate, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("end date must not be null");
    }

    @Test
    void rejectsAStartDateAfterTheEndDate() {
        LocalDate startDate = LocalDate.of(2026, Month.SEPTEMBER, 7);
        LocalDate endDate = LocalDate.of(2026, Month.SEPTEMBER, 6);

        assertThatThrownBy(() -> DateRange.of(startDate, endDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("start date must not be after end date");
    }

    @Test
    void containsBothBoundariesAndDatesBetweenThem() {
        DateRange range =
                DateRange.of(
                        LocalDate.of(2026, Month.SEPTEMBER, 1),
                        LocalDate.of(2026, Month.SEPTEMBER, 6));

        assertThat(range.contains(LocalDate.of(2026, Month.SEPTEMBER, 1))).isTrue();
        assertThat(range.contains(LocalDate.of(2026, Month.SEPTEMBER, 3))).isTrue();
        assertThat(range.contains(LocalDate.of(2026, Month.SEPTEMBER, 6))).isTrue();
    }

    @Test
    void doesNotContainDatesOutsideItsBoundaries() {
        DateRange range =
                DateRange.of(
                        LocalDate.of(2026, Month.SEPTEMBER, 1),
                        LocalDate.of(2026, Month.SEPTEMBER, 6));

        assertThat(range.contains(LocalDate.of(2026, Month.AUGUST, 31))).isFalse();
        assertThat(range.contains(LocalDate.of(2026, Month.SEPTEMBER, 7))).isFalse();
    }

    @Test
    void rejectsANullContainedDate() {
        DateRange range =
                DateRange.of(
                        LocalDate.of(2026, Month.SEPTEMBER, 1),
                        LocalDate.of(2026, Month.SEPTEMBER, 6));

        assertThatThrownBy(() -> range.contains(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("date must not be null");
    }
}
