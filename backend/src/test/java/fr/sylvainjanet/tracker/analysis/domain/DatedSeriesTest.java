package fr.sylvainjanet.tracker.analysis.domain;

import static fr.sylvainjanet.tracker.shared.domain.builder.DateRangeBuilder.aDateRange;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.shared.domain.DateRange;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DatedSeriesTest {

    private static final LocalDate TIMELINE_START = LocalDate.of(2026, 9, 20);
    private static final LocalDate TIMELINE_END = LocalDate.of(2026, 9, 30);
    private static final DateRange RANGE =
            aDateRange()
                    .withStartDate(LocalDate.of(2026, 9, 22))
                    .withEndDate(LocalDate.of(2026, 9, 25))
                    .build();

    @Test
    void createsAGenericDatedSeries() {
        DatedValue value = new DatedValue(LocalDate.of(2026, 9, 23), new BigDecimal("12.34"));

        DatedSeries series =
                DatedSeries.create(TIMELINE_START, TIMELINE_END, RANGE, List.of(value));

        assertThat(series.timelineStartDate()).isEqualTo(TIMELINE_START);
        assertThat(series.timelineEndDate()).isEqualTo(TIMELINE_END);
        assertThat(series.range()).isEqualTo(RANGE);
        assertThat(series.values()).containsExactly(value);
    }

    @Test
    void rejectsATimelineEndingBeforeItStarts() {
        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_END, TIMELINE_START, RANGE, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("timeline start date must not be after timeline end date");
    }

    @Test
    void rejectsARepresentedRangeStartingBeforeTheTimeline() {
        DateRange range =
                aDateRange()
                        .withStartDate(TIMELINE_START.minusDays(1))
                        .withEndDate(RANGE.getEndDate())
                        .build();

        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_START, TIMELINE_END, range, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("represented range must not start before the timeline start date");
    }

    @Test
    void rejectsARepresentedRangeEndingAfterTheTimeline() {
        DateRange range =
                aDateRange()
                        .withStartDate(RANGE.getStartDate())
                        .withEndDate(TIMELINE_END.plusDays(1))
                        .build();

        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_START, TIMELINE_END, range, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("represented range must not end after the timeline end date");
    }

    @Test
    void rejectsAValueBeforeTheRepresentedRange() {
        DatedValue value =
                new DatedValue(RANGE.getStartDate().minusDays(1), new BigDecimal("12.34"));

        List<DatedValue> values = List.of(value);
        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_START, TIMELINE_END, RANGE, values))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be inside the represented range");
    }

    @Test
    void rejectsAValueAfterTheRepresentedRange() {
        DatedValue value = new DatedValue(RANGE.getEndDate().plusDays(1), new BigDecimal("12.34"));
        List<DatedValue> values = List.of(value);

        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_START, TIMELINE_END, RANGE, values))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be inside the represented range");
    }

    @Test
    void rejectsDuplicateValueDates() {
        List<DatedValue> values =
                List.of(
                        new DatedValue(LocalDate.of(2026, 9, 23), new BigDecimal("12.34")),
                        new DatedValue(LocalDate.of(2026, 9, 23), new BigDecimal("56.78")));

        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_START, TIMELINE_END, RANGE, values))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("values must be ordered by unique ascending dates");
    }

    @Test
    void rejectsDescendingValueDates() {
        List<DatedValue> values =
                List.of(
                        new DatedValue(LocalDate.of(2026, 9, 24), new BigDecimal("12.34")),
                        new DatedValue(LocalDate.of(2026, 9, 23), new BigDecimal("56.78")));

        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_START, TIMELINE_END, RANGE, values))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("values must be ordered by unique ascending dates");
    }

    @Test
    void rejectsNullInputs() {
        assertThatThrownBy(() -> DatedSeries.create(null, TIMELINE_END, RANGE, List.of()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("timeline start date must not be null");

        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_START, null, RANGE, List.of()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("timeline end date must not be null");

        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_START, TIMELINE_END, null, List.of()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("represented range must not be null");

        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_START, TIMELINE_END, RANGE, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("values must not be null");
    }

    @Test
    void rejectsANullValue() {
        List<DatedValue> values = singletonList(null);

        assertThatThrownBy(() -> DatedSeries.create(TIMELINE_START, TIMELINE_END, RANGE, values))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null");
    }

    @Test
    void protectsItsValueCollection() {
        DatedValue value = new DatedValue(LocalDate.of(2026, 9, 23), new BigDecimal("12.34"));
        List<DatedValue> suppliedValues = new ArrayList<>(List.of(value));

        DatedSeries series =
                DatedSeries.create(TIMELINE_START, TIMELINE_END, RANGE, suppliedValues);
        suppliedValues.clear();

        List<DatedValue> values = series.values();
        assertThat(values).containsExactly(value);
        assertThatThrownBy(values::clear).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void indexesTheTimelineStartAsOne() {
        DatedSeries series = emptySeries();

        assertThat(series.indexFor(TIMELINE_START)).isEqualTo(1L);
    }

    @Test
    void derivesIndexesFromElapsedCalendarDays() {
        DatedSeries series = emptySeries();

        assertThat(series.indexFor(LocalDate.of(2026, 9, 23))).isEqualTo(4L);
    }

    @Test
    void rejectsAnIndexDateBeforeTheTimeline() {
        DatedSeries series = emptySeries();

        LocalDate date = TIMELINE_START.minusDays(1);
        assertThatThrownBy(() -> series.indexFor(date))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("date must not be before the timeline start date");
    }

    @Test
    void rejectsAnIndexDateAfterTheTimeline() {
        DatedSeries series = emptySeries();

        LocalDate date = TIMELINE_END.plusDays(1);
        assertThatThrownBy(() -> series.indexFor(date))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("date must not be after the timeline end date");
    }

    @Test
    void rejectsANullIndexDate() {
        DatedSeries series = emptySeries();

        assertThatThrownBy(() -> series.indexFor(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("date must not be null");
    }

    private static DatedSeries emptySeries() {
        return DatedSeries.create(TIMELINE_START, TIMELINE_END, RANGE, List.of());
    }
}
