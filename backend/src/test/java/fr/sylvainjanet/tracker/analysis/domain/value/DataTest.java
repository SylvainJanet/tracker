package fr.sylvainjanet.tracker.analysis.domain.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.shared.domain.DateRange;
import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class DataTest {

    private static final LocalDate COMPLETE_RANGE_START = LocalDate.of(2026, Month.SEPTEMBER, 20);
    private static final LocalDate COMPLETE_RANGE_END = LocalDate.of(2026, Month.SEPTEMBER, 30);
    private static final LocalDate ANALYSIS_RANGE_START = LocalDate.of(2026, Month.SEPTEMBER, 22);
    private static final LocalDate ANALYSIS_RANGE_END = LocalDate.of(2026, Month.SEPTEMBER, 25);
    private static final DateRange COMPLETE_RANGE =
            DateRange.create(COMPLETE_RANGE_START, COMPLETE_RANGE_END);
    private static final DateRange ANALYSIS_RANGE =
            DateRange.create(ANALYSIS_RANGE_START, ANALYSIS_RANGE_END);
    private static final DatedValue FIRST_VALUE =
            DatedValue.create(ANALYSIS_RANGE_START, new BigDecimal("12.34"));
    private static final DatedValue LAST_VALUE =
            DatedValue.create(ANALYSIS_RANGE_END, new BigDecimal("56.78"));
    private static final DatedSeries DATA_SERIES =
            DatedSeries.create(List.of(FIRST_VALUE, LAST_VALUE));

    @Test
    void createsDataForAnAnalysisRangeWithinTheCompleteDataRange() {
        Data data = Data.create(COMPLETE_RANGE, ANALYSIS_RANGE, DATA_SERIES);

        assertThat(data.timelineStartDate()).isEqualTo(COMPLETE_RANGE_START);
        assertThat(data.analysisRange()).isEqualTo(ANALYSIS_RANGE);
        assertThat(data.dataSeries()).isEqualTo(DATA_SERIES);
    }

    @Test
    void createsCompleteDataUsingTheSameRangeForDataAndAnalysis() {
        Data data = Data.createComplete(COMPLETE_RANGE, List.of(FIRST_VALUE, LAST_VALUE));

        assertThat(data.timelineStartDate()).isEqualTo(COMPLETE_RANGE_START);
        assertThat(data.analysisRange()).isEqualTo(COMPLETE_RANGE);
        assertThat(data.dataSeries()).containsExactly(FIRST_VALUE, LAST_VALUE);
    }

    @Test
    void acceptsAnEmptyDataSeries() {
        assertThatCode(
                        () ->
                                Data.create(
                                        COMPLETE_RANGE,
                                        ANALYSIS_RANGE,
                                        DatedSeries.create(List.of())))
                .doesNotThrowAnyException();
    }

    @Test
    void acceptsDataOutsideTheAnalysisRangeWhenItIsWithinTheCompleteDataRange() {
        DatedValue beforeAnalysis =
                DatedValue.create(ANALYSIS_RANGE_START.minusDays(1), new BigDecimal("12.34"));
        DatedValue afterAnalysis =
                DatedValue.create(ANALYSIS_RANGE_END.plusDays(1), new BigDecimal("56.78"));
        DatedSeries series = DatedSeries.create(List.of(beforeAnalysis, afterAnalysis));

        assertThatCode(() -> Data.create(COMPLETE_RANGE, ANALYSIS_RANGE, series))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsMissingInputsTogether() {
        assertThatThrownBy(() -> Data.create(null, null, null))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("dataCompleteRange must not be null")
                .hasMessageContaining("analysisRange must not be null")
                .hasMessageContaining("dataSeries must not be null");
    }

    @ParameterizedTest
    @MethodSource("analysisRangesOutsideCompleteRange")
    void rejectsAnAnalysisRangeOutsideTheCompleteDataRange(DateRange analysisRange) {
        assertThatThrownBy(() -> Data.create(COMPLETE_RANGE, analysisRange, DATA_SERIES))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("analysisRange must be within dataCompleteRange");
    }

    @ParameterizedTest
    @MethodSource("dataSeriesOutsideCompleteRange")
    void rejectsADataSeriesOutsideTheCompleteDataRange(DatedSeries dataSeries) {
        assertThatThrownBy(() -> Data.create(COMPLETE_RANGE, ANALYSIS_RANGE, dataSeries))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("dataSeries date range must be within dataCompleteRange");
    }

    @ParameterizedTest
    @MethodSource("timelineDatesAndIndexes")
    void mapsTimelineDatesAndIndexesInBothDirections(LocalDate date, long index) {
        Data data = validData();

        assertThat(data.indexFor(date)).isEqualTo(index);
        assertThat(data.dateForIndex(index)).isEqualTo(date);
    }

    @Test
    void rejectsAnIndexDateBeforeTheTimeline() {
        Data data = validData();
        LocalDate date = COMPLETE_RANGE_START.minusDays(1);

        assertThatThrownBy(() -> data.indexFor(date))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("date must not be before the timeline start date");
    }

    @Test
    void rejectsAnIndexDateAfterTheTimeline() {
        Data data = validData();
        LocalDate date = COMPLETE_RANGE_END.plusDays(1);

        assertThatThrownBy(() -> data.indexFor(date))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("date must not be after the timeline end date");
    }

    @Test
    void rejectsANullIndexDate() {
        Data data = validData();

        assertThatThrownBy(() -> data.indexFor(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("date must not be null");
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L})
    void rejectsAnIndexBeforeTheTimelineStart(long index) {
        Data data = validData();

        assertThatThrownBy(() -> data.dateForIndex(index))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("index must be greater than or equal to 1");
    }

    @Test
    void rejectsAnIndexAfterTheTimelineEnd() {
        Data data = validData();

        assertThatThrownBy(() -> data.dateForIndex(12L))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("index must not be after the timeline end date");
    }

    @Test
    void hasValueSemantics() {
        Data first = validData();
        Data equal = validData();
        Data differentCompleteRange =
                Data.create(
                        DateRange.create(COMPLETE_RANGE_START.minusDays(1), COMPLETE_RANGE_END),
                        ANALYSIS_RANGE,
                        DATA_SERIES);
        Data differentAnalysisRange =
                Data.create(
                        COMPLETE_RANGE,
                        DateRange.create(ANALYSIS_RANGE_START.plusDays(1), ANALYSIS_RANGE_END),
                        DATA_SERIES);
        Data differentDataSeries =
                Data.create(
                        COMPLETE_RANGE, ANALYSIS_RANGE, DatedSeries.create(List.of(FIRST_VALUE)));

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(differentCompleteRange)
                .isNotEqualTo(differentAnalysisRange)
                .isNotEqualTo(differentDataSeries)
                .isNotNull();
    }

    @Test
    void hasAnExplicitStringRepresentation() {
        Data data = validData();

        assertThat(data)
                .hasToString(
                        "Data{dataCompleteRange=DateRange{startDate=2026-09-20, "
                                + "endDate=2026-09-30}, analysisRange=DateRange{startDate="
                                + "2026-09-22, endDate=2026-09-25}, dataSeries="
                                + "UnmodifiableList{list=[DatedValue[date=2026-09-22, "
                                + "value=12.34], DatedValue[date=2026-09-25, value=56.78]]}}");
    }

    private static Data validData() {
        return Data.create(COMPLETE_RANGE, ANALYSIS_RANGE, DATA_SERIES);
    }

    private static Stream<DateRange> analysisRangesOutsideCompleteRange() {
        return Stream.of(
                DateRange.create(COMPLETE_RANGE_START.minusDays(1), ANALYSIS_RANGE_END),
                DateRange.create(ANALYSIS_RANGE_START, COMPLETE_RANGE_END.plusDays(1)));
    }

    private static Stream<DatedSeries> dataSeriesOutsideCompleteRange() {
        return Stream.of(
                DatedSeries.create(
                        List.of(
                                DatedValue.create(
                                        COMPLETE_RANGE_START.minusDays(1),
                                        new BigDecimal("12.34")))),
                DatedSeries.create(
                        List.of(
                                DatedValue.create(
                                        COMPLETE_RANGE_END.plusDays(1), new BigDecimal("56.78")))));
    }

    private static Stream<Arguments> timelineDatesAndIndexes() {
        return Stream.of(
                Arguments.of(COMPLETE_RANGE_START, 1L),
                Arguments.of(LocalDate.of(2026, Month.SEPTEMBER, 23), 4L),
                Arguments.of(COMPLETE_RANGE_END, 11L));
    }
}
