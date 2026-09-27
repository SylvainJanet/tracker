package fr.sylvainjanet.tracker.analysis.domain;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.analysis.domain.value.DatedSeries;
import fr.sylvainjanet.tracker.analysis.domain.value.DatedValue;
import fr.sylvainjanet.tracker.shared.domain.DateRange;
import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class DatedSeriesTest {

    private static final LocalDate TIMELINE_START = LocalDate.of(2026, Month.SEPTEMBER, 20);
    private static final LocalDate TIMELINE_END = LocalDate.of(2026, Month.SEPTEMBER, 30);
    private static final LocalDate RANGE_START = LocalDate.of(2026, Month.SEPTEMBER, 22);
    private static final LocalDate RANGE_END = LocalDate.of(2026, Month.SEPTEMBER, 25);
    private static final DateRange RANGE = DateRange.create(RANGE_START, RANGE_END);
    private static final DatedValue FIRST_VALUE =
            DatedValue.create(RANGE_START, new BigDecimal("12.34"));
    private static final DatedValue LAST_VALUE =
            DatedValue.create(RANGE_END, new BigDecimal("56.78"));

    @Test
    void createsADatedSeries() {
        DatedSeries series =
                DatedSeries.create(
                        TIMELINE_START, TIMELINE_END, RANGE, List.of(FIRST_VALUE, LAST_VALUE));

        assertThat(series.timelineStartDate()).isEqualTo(TIMELINE_START);
        assertThat(series.timelineEndDate()).isEqualTo(TIMELINE_END);
        assertThat(series.range()).isEqualTo(RANGE);
        assertThat(series.values()).containsExactly(FIRST_VALUE, LAST_VALUE);
    }

    @Test
    void createsACompleteSeriesWhoseTimelineMatchesItsRange() {
        DatedSeries series = DatedSeries.completeSeries(RANGE, List.of(FIRST_VALUE, LAST_VALUE));

        assertThat(series.timelineStartDate()).isEqualTo(RANGE_START);
        assertThat(series.timelineEndDate()).isEqualTo(RANGE_END);
        assertThat(series.range()).isEqualTo(RANGE);
        assertThat(series.values()).containsExactly(FIRST_VALUE, LAST_VALUE);
    }

    @Test
    void acceptsAnEmptySeries() {
        assertThatCode(() -> DatedSeries.create(TIMELINE_START, TIMELINE_END, RANGE, List.of()))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsMissingInputsTogether() {
        assertThatThrownBy(() -> DatedSeries.create(null, null, null, null))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("timeline start date must not be null")
                .hasMessageContaining("timeline end date must not be null")
                .hasMessageContaining("represented dateRange must not be null")
                .hasMessageContaining("values must not be null");
    }

    @Test
    void rejectsATimelineEndingBeforeItStarts() {
        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_END, TIMELINE_START, RANGE, List.of()))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("timeline start date must not be after timeline end date");
    }

    @Test
    void rejectsARepresentedRangeStartingBeforeTheTimeline() {
        DateRange range = DateRange.create(TIMELINE_START.minusDays(1), RANGE_END);

        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_START, TIMELINE_END, range, List.of()))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining(
                        "represented dateRange must not start before the timeline start date");
    }

    @Test
    void rejectsARepresentedRangeEndingAfterTheTimeline() {
        DateRange range = DateRange.create(RANGE_START, TIMELINE_END.plusDays(1));

        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_START, TIMELINE_END, range, List.of()))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining(
                        "represented dateRange must not end after the timeline end date");
    }

    @Test
    void rejectsAValueBeforeTheRepresentedRange() {
        DatedValue value = DatedValue.create(RANGE_START.minusDays(1), new BigDecimal("12.34"));

        List<DatedValue> values = List.of(value);
        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_START, TIMELINE_END, RANGE, values))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("value must be inside the represented dateRange");
    }

    @Test
    void rejectsAValueAfterTheRepresentedRange() {
        DatedValue value = DatedValue.create(RANGE_END.plusDays(1), new BigDecimal("12.34"));

        List<DatedValue> values = List.of(value);
        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_START, TIMELINE_END, RANGE, values))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("value must be inside the represented dateRange");
    }

    @Test
    void rejectsDuplicateValueDates() {
        List<DatedValue> values =
                List.of(FIRST_VALUE, DatedValue.create(RANGE_START, new BigDecimal("56.78")));

        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_START, TIMELINE_END, RANGE, values))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("values must be ordered by unique ascending dates");
    }

    @Test
    void rejectsDescendingValueDates() {
        List<DatedValue> values = List.of(LAST_VALUE, FIRST_VALUE);

        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_START, TIMELINE_END, RANGE, values))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("values must be ordered by unique ascending dates");
    }

    @Test
    void rejectsANullValue() {
        List<DatedValue> values = singletonList(null);

        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_START, TIMELINE_END, RANGE, values))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("value must not be null");
    }

    @Test
    void protectsItsValuesFromMutation() {
        List<DatedValue> suppliedValues = new ArrayList<>(List.of(FIRST_VALUE));
        DatedSeries series =
                DatedSeries.create(TIMELINE_START, TIMELINE_END, RANGE, suppliedValues);

        suppliedValues.clear();

        List<DatedValue> values = series.values();
        assertThat(values).containsExactly(FIRST_VALUE);
        assertThatThrownBy(values::clear).isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("timelineDatesAndIndexes")
    void mapsTimelineDatesAndIndexesInBothDirections(LocalDate date, long index) {
        DatedSeries series = emptySeries();

        assertThat(series.indexFor(date)).isEqualTo(index);
        assertThat(series.dateForIndex(index)).isEqualTo(date);
    }

    @Test
    void rejectsAnIndexDateBeforeTheTimeline() {
        DatedSeries series = emptySeries();

        LocalDate date = TIMELINE_START.minusDays(1);
        assertThatThrownBy(() -> series.indexFor(date))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("date must not be before the timeline start date");
    }

    @Test
    void rejectsAnIndexDateAfterTheTimeline() {
        DatedSeries series = emptySeries();

        LocalDate date = TIMELINE_END.plusDays(1);
        assertThatThrownBy(() -> series.indexFor(date))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("date must not be after the timeline end date");
    }

    @Test
    void rejectsANullIndexDate() {
        DatedSeries series = emptySeries();

        assertThatThrownBy(() -> series.indexFor(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("date must not be null");
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L})
    void rejectsAnIndexBeforeTheTimelineStart(long index) {
        DatedSeries series = emptySeries();

        assertThatThrownBy(() -> series.dateForIndex(index))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("index must be greater than or equal to 1");
    }

    @Test
    void rejectsAnIndexAfterTheTimelineEnd() {
        DatedSeries series = emptySeries();

        assertThatThrownBy(() -> series.dateForIndex(12L))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("index must not be after the timeline end date");
    }

    @Test
    void hasValueSemantics() {
        DatedSeries first = emptySeries();
        DatedSeries equal = emptySeries();
        DatedSeries differentTimelineStart =
                DatedSeries.create(TIMELINE_START.minusDays(1), TIMELINE_END, RANGE, List.of());
        DatedSeries differentTimelineEnd =
                DatedSeries.create(TIMELINE_START, TIMELINE_END.plusDays(1), RANGE, List.of());
        DatedSeries differentRange =
                DatedSeries.create(
                        TIMELINE_START,
                        TIMELINE_END,
                        DateRange.create(RANGE_START, RANGE_END.minusDays(1)),
                        List.of());
        DatedSeries differentValues =
                DatedSeries.create(TIMELINE_START, TIMELINE_END, RANGE, List.of(FIRST_VALUE));

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(differentTimelineStart)
                .isNotEqualTo(differentTimelineEnd)
                .isNotEqualTo(differentRange)
                .isNotEqualTo(differentValues)
                .isNotNull();
    }

    private static DatedSeries emptySeries() {
        return DatedSeries.create(TIMELINE_START, TIMELINE_END, RANGE, List.of());
    }

    private static Stream<Arguments> timelineDatesAndIndexes() {
        return Stream.of(
                Arguments.of(TIMELINE_START, 1L),
                Arguments.of(LocalDate.of(2026, Month.SEPTEMBER, 23), 4L),
                Arguments.of(TIMELINE_END, 11L));
    }
}
