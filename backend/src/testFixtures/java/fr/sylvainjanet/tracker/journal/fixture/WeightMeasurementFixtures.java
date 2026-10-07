package fr.sylvainjanet.tracker.journal.fixture;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.command.LogWeightMeasurementCommand;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementByDateQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementInDateRangeQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetFirstWeightMeasurementDateResult;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.WeightMeasurementResult;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.GetWeightMeasurementByDateCriteria;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.GetWeightMeasurementInDateRangeCriteria;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.instruction.LogWeightMeasurementInstruction;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetFirstWeightMeasurementDateOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetWeightMeasurementInDateRangeOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.WeightMeasurementOutcome;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public final class WeightMeasurementFixtures {

    public static final LocalDate START_DATE = LocalDate.of(2026, Month.SEPTEMBER, 1);
    public static final LocalDate MIDDLE_DATE = LocalDate.of(2026, Month.SEPTEMBER, 3);
    public static final LocalDate END_DATE = LocalDate.of(2026, Month.SEPTEMBER, 6);
    public static final LocalDate BEFORE_START_DATE = START_DATE.minusDays(1);
    public static final LocalDate AFTER_END_DATE = END_DATE.plusDays(1);

    public static final BigDecimal START_WEIGHT_IN_KG = new BigDecimal("82.10");
    public static final BigDecimal MIDDLE_WEIGHT_IN_KG = new BigDecimal("81.95");
    public static final BigDecimal END_WEIGHT_IN_KG = new BigDecimal("81.80");
    public static final BigDecimal BEFORE_START_WEIGHT_IN_KG = new BigDecimal("82.30");
    public static final BigDecimal AFTER_END_WEIGHT_IN_KG = new BigDecimal("81.70");
    public static final BigDecimal INVALID_WEIGHT_IN_KG = new BigDecimal("-82.10");

    public static final LogWeightMeasurementCommand LOG_COMMAND =
            new LogWeightMeasurementCommand(START_DATE, START_WEIGHT_IN_KG);
    public static final LogWeightMeasurementCommand INVALID_LOG_COMMAND =
            new LogWeightMeasurementCommand(START_DATE, INVALID_WEIGHT_IN_KG);
    public static final LogWeightMeasurementInstruction LOG_INSTRUCTION =
            new LogWeightMeasurementInstruction(START_DATE, START_WEIGHT_IN_KG);
    public static final LogWeightMeasurementInstruction MIDDLE_LOG_INSTRUCTION =
            new LogWeightMeasurementInstruction(MIDDLE_DATE, MIDDLE_WEIGHT_IN_KG);
    public static final LogWeightMeasurementInstruction END_LOG_INSTRUCTION =
            new LogWeightMeasurementInstruction(END_DATE, END_WEIGHT_IN_KG);
    public static final LogWeightMeasurementInstruction BEFORE_RANGE_LOG_INSTRUCTION =
            new LogWeightMeasurementInstruction(BEFORE_START_DATE, BEFORE_START_WEIGHT_IN_KG);
    public static final LogWeightMeasurementInstruction AFTER_RANGE_LOG_INSTRUCTION =
            new LogWeightMeasurementInstruction(AFTER_END_DATE, AFTER_END_WEIGHT_IN_KG);
    public static final LogWeightMeasurementInstruction UPDATE_LOG_INSTRUCTION =
            new LogWeightMeasurementInstruction(START_DATE, END_WEIGHT_IN_KG);

    public static final GetWeightMeasurementByDateQuery BY_DATE_QUERY =
            new GetWeightMeasurementByDateQuery(START_DATE);
    public static final GetWeightMeasurementByDateCriteria BY_DATE_CRITERIA =
            new GetWeightMeasurementByDateCriteria(START_DATE);

    public static final GetWeightMeasurementInDateRangeQuery DATE_RANGE_QUERY =
            new GetWeightMeasurementInDateRangeQuery(START_DATE, END_DATE);
    public static final GetWeightMeasurementInDateRangeCriteria DATE_RANGE_CRITERIA =
            new GetWeightMeasurementInDateRangeCriteria(START_DATE, END_DATE);

    public static final WeightMeasurementOutcome START_OUTCOME =
            new WeightMeasurementOutcome(START_DATE, START_WEIGHT_IN_KG);
    public static final WeightMeasurementOutcome MIDDLE_OUTCOME =
            new WeightMeasurementOutcome(MIDDLE_DATE, MIDDLE_WEIGHT_IN_KG);
    public static final WeightMeasurementOutcome END_OUTCOME =
            new WeightMeasurementOutcome(END_DATE, END_WEIGHT_IN_KG);
    public static final WeightMeasurementOutcome INVALID_OUTCOME =
            new WeightMeasurementOutcome(START_DATE, INVALID_WEIGHT_IN_KG);
    public static final WeightMeasurementOutcome UPDATED_START_OUTCOME =
            new WeightMeasurementOutcome(START_DATE, END_WEIGHT_IN_KG);

    public static final WeightMeasurementResult START_RESULT =
            new WeightMeasurementResult(START_DATE, START_WEIGHT_IN_KG);
    public static final WeightMeasurementResult MIDDLE_RESULT =
            new WeightMeasurementResult(MIDDLE_DATE, MIDDLE_WEIGHT_IN_KG);
    public static final WeightMeasurementResult END_RESULT =
            new WeightMeasurementResult(END_DATE, END_WEIGHT_IN_KG);

    public static final GetWeightMeasurementInDateRangeOutcome DATE_RANGE_OUTCOME =
            new GetWeightMeasurementInDateRangeOutcome(
                    List.of(START_OUTCOME, MIDDLE_OUTCOME, END_OUTCOME));
    public static final GetWeightMeasurementInDateRangeOutcome EMPTY_DATE_RANGE_OUTCOME =
            new GetWeightMeasurementInDateRangeOutcome(List.of());

    public static final GetWeightMeasurementInDateRangeResult DATE_RANGE_RESULT =
            new GetWeightMeasurementInDateRangeResult(
                    List.of(START_RESULT, MIDDLE_RESULT, END_RESULT));
    public static final GetWeightMeasurementInDateRangeResult EMPTY_DATE_RANGE_RESULT =
            new GetWeightMeasurementInDateRangeResult(List.of());

    public static final GetFirstWeightMeasurementDateOutcome FIRST_DATE_OUTCOME =
            new GetFirstWeightMeasurementDateOutcome(START_DATE);
    public static final GetFirstWeightMeasurementDateResult FIRST_DATE_RESULT =
            new GetFirstWeightMeasurementDateResult(START_DATE);

    private WeightMeasurementFixtures() {}
}
