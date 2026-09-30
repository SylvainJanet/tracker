package fr.sylvainjanet.tracker.statistics.fixture;

import static fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculationRoundingResult.PRECISE;
import static fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculationRoundingResult.PRETTY;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.IndexedValueCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.ApproximationResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.FractionResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.RollingAveragePointResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.RollingAverageResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.ValueResult;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public final class RollingAverageFixtures {

    public static final CalculateRollingAveragesCommand EMPTY_VALUES_COMMAND =
            new CalculateRollingAveragesCommand(List.of(), List.of(3L), 1L, 6L);
    public static final CalculateRollingAveragesCommand NO_WINDOWS_COMMAND =
            new CalculateRollingAveragesCommand(
                    List.of(new IndexedValueCommand(1L, new BigDecimal("80.00"))),
                    List.of(),
                    1L,
                    6L);

    public static final CalculateRollingAveragesCommand EXACT_AVERAGE_COMMAND =
            new CalculateRollingAveragesCommand(
                    List.of(
                            new IndexedValueCommand(3L, new BigDecimal("81.00")),
                            new IndexedValueCommand(1L, new BigDecimal("80.00")),
                            new IndexedValueCommand(2L, new BigDecimal("80.00"))),
                    List.of(3L),
                    3L,
                    3L);
    public static final ValueResult EXACT_AVERAGE_VALUE_RESULT =
            new ValueResult(
                    new FractionResult(new BigDecimal("241.00"), new BigDecimal("3")),
                    Set.of(
                            new ApproximationResult(
                                    new BigDecimal("80.33333333333333333333"), PRECISE),
                            new ApproximationResult(new BigDecimal("80.33"), PRETTY)));
    public static final CalculateRollingAveragesResult EXACT_AVERAGE_RESULT =
            new CalculateRollingAveragesResult(
                    List.of(
                            new RollingAverageResult(
                                    3L,
                                    List.of(
                                            new RollingAveragePointResult(
                                                    3L,
                                                    Set.of(1L, 2L, 3L),
                                                    EXACT_AVERAGE_VALUE_RESULT)))));

    public static final CalculateRollingAveragesCommand THROUGH_REQUESTED_END_COMMAND =
            new CalculateRollingAveragesCommand(
                    List.of(new IndexedValueCommand(3L, new BigDecimal("30"))),
                    List.of(3L),
                    1L,
                    6L);
    public static final ValueResult THIRTY_VALUE_RESULT =
            new ValueResult(
                    new FractionResult(new BigDecimal("30"), BigDecimal.ONE),
                    Set.of(
                            new ApproximationResult(
                                    new BigDecimal("30.00000000000000000000"), PRECISE),
                            new ApproximationResult(new BigDecimal("30.00"), PRETTY)));
    public static final CalculateRollingAveragesResult THROUGH_REQUESTED_END_RESULT =
            new CalculateRollingAveragesResult(
                    List.of(
                            new RollingAverageResult(
                                    3L,
                                    List.of(
                                            new RollingAveragePointResult(
                                                    3L, Set.of(3L), THIRTY_VALUE_RESULT),
                                            new RollingAveragePointResult(
                                                    4L, Set.of(3L), THIRTY_VALUE_RESULT),
                                            new RollingAveragePointResult(
                                                    5L, Set.of(3L), THIRTY_VALUE_RESULT)))));

    public static final CalculateRollingAveragesCommand INVALID_WINDOW_COMMAND =
            new CalculateRollingAveragesCommand(
                    List.of(new IndexedValueCommand(1L, new BigDecimal("80.00"))),
                    List.of(0L),
                    1L,
                    1L);
    public static final CalculateRollingAveragesCommand INVALID_OUTPUT_RANGE_COMMAND =
            new CalculateRollingAveragesCommand(
                    List.of(new IndexedValueCommand(1L, new BigDecimal("80.00"))),
                    List.of(3L),
                    6L,
                    1L);

    public static final CalculateRollingAveragesResult EMPTY_RESULT =
            CalculateRollingAveragesResult.empty();

    private RollingAverageFixtures() {}
}
