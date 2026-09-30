package fr.sylvainjanet.tracker.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;

class DateRangeTest {

    private static final LocalDate START_DATE = LocalDate.of(2026, Month.SEPTEMBER, 1);
    private static final LocalDate END_DATE = LocalDate.of(2026, Month.SEPTEMBER, 6);

    @Test
    void createsAnInclusiveDateRange() {
        DateRange range = DateRange.create(START_DATE, END_DATE);

        assertThat(range.startDate()).isEqualTo(START_DATE);
        assertThat(range.endDate()).isEqualTo(END_DATE);
    }

    @Test
    void acceptsTheSameDateAsBothBoundaries() {
        assertThatCode(() -> DateRange.create(START_DATE, START_DATE)).doesNotThrowAnyException();
    }

    @Test
    void rejectsMissingBoundariesTogether() {
        assertThatThrownBy(() -> DateRange.create(null, null))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("start date must not be null")
                .hasMessageContaining("end date must not be null");
    }

    @Test
    void rejectsAStartDateAfterTheEndDate() {
        assertThatThrownBy(() -> DateRange.create(END_DATE, START_DATE))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("start date must not be after end date");
    }

    @Test
    void containsBothBoundariesAndDatesBetweenThem() {
        DateRange range = DateRange.create(START_DATE, END_DATE);

        assertThat(range.contains(START_DATE)).isTrue();
        assertThat(range.contains(START_DATE.plusDays(2))).isTrue();
        assertThat(range.contains(END_DATE)).isTrue();
    }

    @Test
    void doesNotContainDatesOutsideItsBoundaries() {
        DateRange range = DateRange.create(START_DATE, END_DATE);

        assertThat(range.contains(START_DATE.minusDays(1))).isFalse();
        assertThat(range.contains(END_DATE.plusDays(1))).isFalse();
    }

    @Test
    void rejectsANullContainedDate() {
        DateRange range = DateRange.create(START_DATE, END_DATE);

        assertThatThrownBy(() -> range.contains(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("date must not be null");
    }

    @Test
    void hasValueSemantics() {
        DateRange first = DateRange.create(START_DATE, END_DATE);
        DateRange equal = DateRange.create(START_DATE, END_DATE);
        DateRange different = DateRange.create(START_DATE.plusDays(1), END_DATE);

        assertThat(first).isEqualTo(equal).hasSameHashCodeAs(equal).isNotEqualTo(different);
    }
}
