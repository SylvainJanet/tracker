package fr.sylvainjanet.tracker.analysis.fixture;

import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRoundingResult.PRECISE;
import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRoundingResult.PRETTY;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisApproximationResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisDateRangeResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisFractionResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAveragePointResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAverageResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisRollingAverageValueResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisValueResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementInDateRangeQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetFirstWeightMeasurementDateResult;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.WeightMeasurementResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.IndexedValueCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.ApproximationResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculationRoundingResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.FractionResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.RollingAveragePointResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.RollingAverageResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.ValueResult;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

public final class WeightAnalysisFixtures {

    public static final LocalDate FIRST_DATE = LocalDate.of(2026, Month.SEPTEMBER, 20);
    public static final LocalDate SECOND_DATE = LocalDate.of(2026, Month.SEPTEMBER, 23);
    public static final LocalDate TODAY = LocalDate.of(2026, Month.SEPTEMBER, 25);
    public static final LocalDate FUTURE_DATE = TODAY.plusDays(1);

    public static final Clock CLOCK =
            Clock.fixed(Instant.parse("2026-09-25T12:00:00Z"), ZoneOffset.UTC);

    public static final BigDecimal FIRST_WEIGHT_IN_KG = new BigDecimal("82.10");
    public static final BigDecimal SECOND_WEIGHT_IN_KG = new BigDecimal("81.90");

    public static final GetFirstWeightMeasurementDateResult FIRST_DATE_RESULT =
            new GetFirstWeightMeasurementDateResult(FIRST_DATE);
    public static final GetFirstWeightMeasurementDateResult FUTURE_FIRST_DATE_RESULT =
            new GetFirstWeightMeasurementDateResult(FUTURE_DATE);

    public static final GetWeightMeasurementInDateRangeQuery DATE_RANGE_QUERY =
            new GetWeightMeasurementInDateRangeQuery(FIRST_DATE, TODAY);
    public static final GetWeightMeasurementInDateRangeResult MEASUREMENTS_RESULT =
            new GetWeightMeasurementInDateRangeResult(
                    List.of(
                            new WeightMeasurementResult(FIRST_DATE, FIRST_WEIGHT_IN_KG),
                            new WeightMeasurementResult(SECOND_DATE, SECOND_WEIGHT_IN_KG)));
    public static final GetWeightMeasurementInDateRangeResult EMPTY_MEASUREMENTS_RESULT =
            new GetWeightMeasurementInDateRangeResult(List.of());
    public static final GetWeightMeasurementInDateRangeResult OUTSIDE_RANGE_MEASUREMENTS_RESULT =
            new GetWeightMeasurementInDateRangeResult(
                    List.of(new WeightMeasurementResult(FUTURE_DATE, SECOND_WEIGHT_IN_KG)));

    public static final CalculateRollingAveragesCommand ROLLING_AVERAGES_COMMAND =
            new CalculateRollingAveragesCommand(
                    List.of(
                            new IndexedValueCommand(1L, FIRST_WEIGHT_IN_KG),
                            new IndexedValueCommand(4L, SECOND_WEIGHT_IN_KG)),
                    List.of(7L, 14L, 28L, 60L, 180L, 360L),
                    1L,
                    6L);

    public static final ValueResult ROLLING_AVERAGE_VALUE_RESULT =
            new ValueResult(
                    new FractionResult(new BigDecimal("164.00"), new BigDecimal("2")),
                    Set.of(
                            new ApproximationResult(
                                    new BigDecimal("82.00000000000000000000"),
                                    CalculationRoundingResult.PRECISE),
                            new ApproximationResult(
                                    new BigDecimal("82.00"), CalculationRoundingResult.PRETTY)));
    public static final CalculateRollingAveragesResult ROLLING_AVERAGES_RESULT =
            new CalculateRollingAveragesResult(
                    List.of(
                            new RollingAverageResult(
                                    7L,
                                    List.of(
                                            new RollingAveragePointResult(
                                                    6L,
                                                    Set.of(1L, 4L),
                                                    ROLLING_AVERAGE_VALUE_RESULT)))));

    public static final AnalysisValueResult FIRST_ANALYSIS_VALUE =
            new AnalysisValueResult(FIRST_DATE, 1L, FIRST_WEIGHT_IN_KG);
    public static final AnalysisValueResult SECOND_ANALYSIS_VALUE =
            new AnalysisValueResult(SECOND_DATE, 4L, SECOND_WEIGHT_IN_KG);
    public static final AnalysisRollingAverageValueResult ANALYSIS_ROLLING_AVERAGE_VALUE =
            new AnalysisRollingAverageValueResult(
                    new AnalysisFractionResult(new BigDecimal("164.00"), new BigDecimal("2")),
                    Set.of(
                            new AnalysisApproximationResult(
                                    new BigDecimal("82.00000000000000000000"), PRECISE),
                            new AnalysisApproximationResult(new BigDecimal("82.00"), PRETTY)));
    public static final GetWeightAnalysisResult ANALYSIS_RESULT =
            new GetWeightAnalysisResult(
                    FIRST_DATE,
                    new AnalysisDateRangeResult(FIRST_DATE, TODAY),
                    List.of(FIRST_ANALYSIS_VALUE, SECOND_ANALYSIS_VALUE),
                    List.of(
                            new AnalysisRollingAverageResult(
                                    7L,
                                    List.of(
                                            new AnalysisRollingAveragePointResult(
                                                    TODAY,
                                                    6L,
                                                    Set.of(
                                                            FIRST_ANALYSIS_VALUE,
                                                            SECOND_ANALYSIS_VALUE),
                                                    ANALYSIS_ROLLING_AVERAGE_VALUE)))));
    public static final GetWeightAnalysisResult NO_MEASUREMENTS_RESULT =
            GetWeightAnalysisResult.noMeasurements();

    private WeightAnalysisFixtures() {}
}
