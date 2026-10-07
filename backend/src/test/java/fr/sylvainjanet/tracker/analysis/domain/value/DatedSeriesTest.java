package fr.sylvainjanet.tracker.analysis.domain.value;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.shared.domain.DateRange;
import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DatedSeriesTest {

    private static final LocalDate FIRST_DATE = LocalDate.of(2026, Month.SEPTEMBER, 22);
    private static final LocalDate LAST_DATE = LocalDate.of(2026, Month.SEPTEMBER, 25);
    private static final DatedValue FIRST_VALUE =
            DatedValue.create(FIRST_DATE, new BigDecimal("12.34"));
    private static final DatedValue LAST_VALUE =
            DatedValue.create(LAST_DATE, new BigDecimal("56.78"));

    @Test
    void createsAChronologicallyOrderedDatedSeries() {
        DatedSeries series = DatedSeries.create(List.of(FIRST_VALUE, LAST_VALUE));

        assertThat(series).containsExactly(FIRST_VALUE, LAST_VALUE);
    }

    @Test
    void derivesItsInclusiveDateRangeFromItsFirstAndLastValues() {
        DatedSeries series = DatedSeries.create(List.of(FIRST_VALUE, LAST_VALUE));

        assertThat(series.dateRange()).isEqualTo(DateRange.create(FIRST_DATE, LAST_DATE));
    }

    @Test
    void acceptsAnEmptySeriesWithoutADateRange() {
        DatedSeries series = DatedSeries.create(List.of());

        assertThat(series).isEmpty();
        assertThat(series.dateRange()).isNull();
    }

    @Test
    void rejectsDuplicateValueDates() {
        DatedValue duplicateDate = DatedValue.create(FIRST_DATE, new BigDecimal("56.78"));
        List<DatedValue> values = List.of(FIRST_VALUE, duplicateDate);

        assertThatThrownBy(() -> DatedSeries.create(values))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("dated values must be ordered by unique chronological dates");
    }

    @Test
    void rejectsDescendingValueDates() {
        List<DatedValue> values = List.of(LAST_VALUE, FIRST_VALUE);

        assertThatThrownBy(() -> DatedSeries.create(values))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("dated values must be ordered by unique chronological dates");
    }

    @Test
    void rejectsANullDatedValue() {
        List<DatedValue> values = singletonList(null);

        assertThatThrownBy(() -> DatedSeries.create(values))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("dated value must not be null");
    }

    @Test
    void protectsItsValuesFromMutation() {
        List<DatedValue> suppliedValues = new ArrayList<>(List.of(FIRST_VALUE, LAST_VALUE));
        DatedSeries series = DatedSeries.create(suppliedValues);

        suppliedValues.clear();

        assertThat(series).containsExactly(FIRST_VALUE, LAST_VALUE);
        assertThatThrownBy(series::clear).isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void hasListValueSemantics() {
        DatedSeries first = DatedSeries.create(List.of(FIRST_VALUE, LAST_VALUE));
        DatedSeries equal = DatedSeries.create(List.of(FIRST_VALUE, LAST_VALUE));
        DatedSeries different = DatedSeries.create(List.of(FIRST_VALUE));

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(different)
                .isNotNull();
    }

    @Test
    void hasAnExplicitStringRepresentation() {
        DatedSeries series = DatedSeries.create(List.of(FIRST_VALUE, LAST_VALUE));

        assertThat(series)
                .hasToString(
                        "UnmodifiableList{list=[DatedValue[date=2026-09-22, value=12.34], "
                                + "DatedValue[date=2026-09-25, value=56.78]]}");
    }
}
