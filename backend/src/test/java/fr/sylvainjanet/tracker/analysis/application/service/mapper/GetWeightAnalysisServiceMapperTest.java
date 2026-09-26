package fr.sylvainjanet.tracker.analysis.application.service.mapper;

import static fr.sylvainjanet.tracker.analysis.application.service.mapper.GetWeightAnalysisServiceMapper.analysisToStatisticsCommand;
import static fr.sylvainjanet.tracker.analysis.application.service.mapper.GetWeightAnalysisServiceMapper.dateRangeToWeightMeasurementQuery;
import static fr.sylvainjanet.tracker.analysis.application.service.mapper.GetWeightAnalysisServiceMapper.statisticsToAnalysis;
import static fr.sylvainjanet.tracker.analysis.application.service.mapper.GetWeightAnalysisServiceMapper.weightMeasurementsToDatedValues;
import static fr.sylvainjanet.tracker.shared.domain.builder.DateRangeBuilder.aDateRange;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.AnalysisDateRangeResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.AnalysisWeightRollingAverageResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.AnalysisWeightValueResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.WeightRollingAveragePointResult;
import fr.sylvainjanet.tracker.analysis.domain.DatedSeries;
import fr.sylvainjanet.tracker.analysis.domain.DatedValue;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementInDateRangeQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult.WeightMeasurementByDateResult;
import fr.sylvainjanet.tracker.shared.domain.DateRange;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand.IndexedValueCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult.RollingAveragePointResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult.RollingAverageResult;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetWeightAnalysisServiceMapperTest {

    private static final LocalDate FIRST_DATE = LocalDate.parse("2026-09-20");
    private static final LocalDate SECOND_DATE = LocalDate.parse("2026-09-23");
    private static final LocalDate END_DATE = LocalDate.parse("2026-09-25");

    @Test
    void mapsADateRangeToAQuery() {
        DateRange range = aDateRange().withStartDate(FIRST_DATE).withEndDate(END_DATE).build();

        GetWeightMeasurementInDateRangeQuery query = dateRangeToWeightMeasurementQuery(range);

        assertThat(query.startDate()).isEqualTo(FIRST_DATE);
        assertThat(query.endDate()).isEqualTo(END_DATE);
    }

    @Test
    void mapsJournalMeasurementResultsToAnalysisValues() {
        GetWeightMeasurementInDateRangeResult result =
                new GetWeightMeasurementInDateRangeResult(
                        List.of(
                                new WeightMeasurementByDateResult(
                                        FIRST_DATE, new BigDecimal("12.34")),
                                new WeightMeasurementByDateResult(
                                        SECOND_DATE, new BigDecimal("56.78"))));

        List<DatedValue> values = weightMeasurementsToDatedValues(result);

        assertThat(values)
                .containsExactly(
                        new DatedValue(FIRST_DATE, new BigDecimal("12.34")),
                        new DatedValue(SECOND_DATE, new BigDecimal("56.78")));
    }

    @Test
    void mapsAnAnalysisSeriesToAStatisticsCommand() {
        DateRange range = aDateRange().withStartDate(FIRST_DATE).withEndDate(END_DATE).build();
        DatedSeries series =
                DatedSeries.create(
                        FIRST_DATE,
                        END_DATE,
                        range,
                        List.of(
                                new DatedValue(FIRST_DATE, new BigDecimal("82.10")),
                                new DatedValue(SECOND_DATE, new BigDecimal("81.90"))));

        CalculateRollingAveragesCommand command =
                analysisToStatisticsCommand(series, List.of(7, 14));

        assertThat(command.values())
                .containsExactly(
                        new IndexedValueCommand(1L, new BigDecimal("82.10")),
                        new IndexedValueCommand(4L, new BigDecimal("81.90")));
        assertThat(command.windowSizes()).containsExactly(7, 14);
    }

    @Test
    void mapsAnAnalysisSeriesAndStatisticsResultToAWeightAnalysisResult() {
        DateRange range = aDateRange().withStartDate(FIRST_DATE).withEndDate(END_DATE).build();
        DatedSeries series =
                DatedSeries.create(
                        FIRST_DATE,
                        END_DATE,
                        range,
                        List.of(
                                new DatedValue(FIRST_DATE, new BigDecimal("82.10")),
                                new DatedValue(SECOND_DATE, new BigDecimal("81.90"))));
        CalculateRollingAveragesResult statisticsResult =
                new CalculateRollingAveragesResult(
                        List.of(
                                new RollingAverageResult(
                                        7,
                                        List.of(
                                                new RollingAveragePointResult(
                                                        4L, new BigDecimal("82.00")),
                                                new RollingAveragePointResult(
                                                        6L, new BigDecimal("81.75"))))));

        GetWeightAnalysisResult result = statisticsToAnalysis(series, statisticsResult);

        assertThat(result.timelineStartDate()).isEqualTo(FIRST_DATE);

        AnalysisDateRangeResult resultRange = result.dateRange();
        assertThat(resultRange).isNotNull();
        assertThat(resultRange.startDate()).isEqualTo(FIRST_DATE);
        assertThat(resultRange.endDate()).isEqualTo(END_DATE);

        assertThat(result.weightValues()).hasSize(2);

        AnalysisWeightValueResult firstMeasurement = result.weightValues().get(0);
        assertThat(firstMeasurement.date()).isEqualTo(FIRST_DATE);
        assertThat(firstMeasurement.dayNumber()).isEqualTo(1L);
        assertThat(firstMeasurement.weightInKg()).isEqualByComparingTo("82.10");

        AnalysisWeightValueResult secondMeasurement = result.weightValues().get(1);
        assertThat(secondMeasurement.date()).isEqualTo(SECOND_DATE);
        assertThat(secondMeasurement.dayNumber()).isEqualTo(4L);
        assertThat(secondMeasurement.weightInKg()).isEqualByComparingTo("81.90");

        assertThat(result.rollingAverages())
                .containsExactly(
                        new AnalysisWeightRollingAverageResult(
                                7,
                                List.of(
                                        new WeightRollingAveragePointResult(
                                                SECOND_DATE, 4L, new BigDecimal("82.00")),
                                        new WeightRollingAveragePointResult(
                                                END_DATE, 6L, new BigDecimal("81.75")))));
    }

    @Test
    void rejectsANullDateRange() {
        assertThatThrownBy(() -> dateRangeToWeightMeasurementQuery(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("date range must not be null");
    }

    @Test
    void rejectsANullWeightMeasurementResult() {
        assertThatThrownBy(() -> weightMeasurementsToDatedValues(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("weight measurement result must not be null");
    }

    @Test
    void rejectsANullSeriesWhenMappingAStatisticsResult() {
        assertThatThrownBy(() -> statisticsToAnalysis(null, CalculateRollingAveragesResult.empty()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("series must not be null");
    }

    @Test
    void rejectsANullStatisticsResult() {
        assertThatThrownBy(() -> statisticsToAnalysis(aSeries(), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("statistics result must not be null");
    }

    @Test
    void rejectsANullSeriesWhenMappingAStatisticsCommand() {
        assertThatThrownBy(() -> analysisToStatisticsCommand(null, List.of()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("series must not be null");
    }

    @Test
    void rejectsNullWindowSizes() {
        assertThatThrownBy(() -> analysisToStatisticsCommand(aSeries(), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("window sizes must not be null");
    }

    private static DatedSeries aSeries() {
        DateRange range = aDateRange().withStartDate(FIRST_DATE).withEndDate(END_DATE).build();

        return DatedSeries.create(FIRST_DATE, END_DATE, range, List.of());
    }
}
