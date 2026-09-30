package fr.sylvainjanet.tracker.statistics.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.statistics.domain.calculator.RollingAverageCalculator;
import fr.sylvainjanet.tracker.statistics.domain.value.Approximation;
import fr.sylvainjanet.tracker.statistics.domain.value.CalculationRounding;
import fr.sylvainjanet.tracker.statistics.domain.value.Fraction;
import fr.sylvainjanet.tracker.statistics.domain.value.IndexRange;
import fr.sylvainjanet.tracker.statistics.domain.value.IndexedSeries;
import fr.sylvainjanet.tracker.statistics.domain.value.IndexedValue;
import fr.sylvainjanet.tracker.statistics.domain.value.RollingAverage;
import fr.sylvainjanet.tracker.statistics.domain.value.RollingAveragePoint;
import fr.sylvainjanet.tracker.statistics.domain.value.RollingAverages;
import fr.sylvainjanet.tracker.statistics.domain.value.RollingWindow;
import fr.sylvainjanet.tracker.statistics.domain.value.RollingWindows;
import fr.sylvainjanet.tracker.statistics.domain.value.Value;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RollingAverageCalculatorTest {

    private static final RollingAverageCalculator CALCULATOR =
            RollingAverageCalculator.getInstance();

    @Test
    void calculatesAnExactFractionAndEveryRequestedApproximation() {
        IndexedSeries series =
                IndexedSeries.create(
                        List.of(
                                indexedValue(1L, "80.00"),
                                indexedValue(2L, "80.00"),
                                indexedValue(3L, "81.00")));

        RollingAverages averages =
                CALCULATOR.calculate(
                        series,
                        RollingWindows.create(List.of(RollingWindow.create(3L))),
                        IndexRange.create(3L, 3L),
                        CalculationRounding.all());

        assertThat(averages).hasSize(1);
        RollingAverage average = averages.getFirst();
        assertThat(average.window()).isEqualTo(RollingWindow.create(3L));
        assertThat(average.points()).hasSize(1);

        RollingAveragePoint point = average.points().getFirst();
        Fraction exactAverage = Fraction.create(new BigDecimal("241.00"), new BigDecimal("3"));
        assertThat(point.index()).isEqualTo(3L);
        assertThat(point.includedIndexes()).containsExactlyInAnyOrder(1L, 2L, 3L);
        assertThat(point.rollingAverage())
                .isEqualTo(Value.createByRounding(exactAverage, CalculationRounding.all()));
    }

    @Test
    void recalculatesAChangingMultiValueWindowAtEveryOutputIndex() {
        RollingAverages averages =
                CALCULATOR.calculate(
                        sevenVaryingValues(),
                        RollingWindows.create(List.of(RollingWindow.create(3L))),
                        IndexRange.create(3L, 7L),
                        CalculationRounding.all());

        assertThat(averages).hasSize(1);
        assertThat(averages.getFirst().points())
                .satisfiesExactly(
                        point ->
                                assertPoint(
                                        point,
                                        3L,
                                        Set.of(1L, 2L, 3L),
                                        "241.20",
                                        "3",
                                        "80.40",
                                        "80.40000000000000000000"),
                        point ->
                                assertPoint(
                                        point,
                                        4L,
                                        Set.of(2L, 3L, 4L),
                                        "243.45",
                                        "3",
                                        "81.15",
                                        "81.15000000000000000000"),
                        point ->
                                assertPoint(
                                        point,
                                        5L,
                                        Set.of(3L, 4L, 5L),
                                        "242.80",
                                        "3",
                                        "80.93",
                                        "80.93333333333333333333"),
                        point ->
                                assertPoint(
                                        point,
                                        6L,
                                        Set.of(4L, 5L, 6L),
                                        "244.00",
                                        "3",
                                        "81.33",
                                        "81.33333333333333333333"),
                        point ->
                                assertPoint(
                                        point,
                                        7L,
                                        Set.of(5L, 6L, 7L),
                                        "241.90",
                                        "3",
                                        "80.63",
                                        "80.63333333333333333333"));
    }

    @Test
    void calculatesDifferentWindowSizesFromTheSameMultiValueSeries() {
        RollingAverages averages =
                CALCULATOR.calculate(
                        sevenVaryingValues(),
                        RollingWindows.create(
                                List.of(
                                        RollingWindow.create(3L),
                                        RollingWindow.create(5L),
                                        RollingWindow.create(7L))),
                        IndexRange.create(7L, 7L),
                        CalculationRounding.all());

        assertThat(averages).hasSize(3);
        assertThat(averages).extracting(RollingAverage::windowSize).containsExactly(3L, 5L, 7L);
        assertPoint(
                averages.get(0).points().getFirst(),
                7L,
                Set.of(5L, 6L, 7L),
                "241.90",
                "3",
                "80.63",
                "80.63333333333333333333");
        assertPoint(
                averages.get(1).points().getFirst(),
                7L,
                Set.of(3L, 4L, 5L, 6L, 7L),
                "404.15",
                "5",
                "80.83",
                "80.83000000000000000000");
        assertPoint(
                averages.get(2).points().getFirst(),
                7L,
                Set.of(1L, 2L, 3L, 4L, 5L, 6L, 7L),
                "565.50",
                "7",
                "80.79",
                "80.78571428571428571429");
    }

    @Test
    void calculatesAPartialWindowFromOnlyTheAvailableSparseValues() {
        IndexedSeries series =
                IndexedSeries.create(
                        List.of(
                                IndexedValue.create(1L, Value.create(10)),
                                IndexedValue.create(3L, Value.create(20))));

        RollingAverages averages =
                CALCULATOR.calculate(
                        series,
                        RollingWindows.create(List.of(RollingWindow.create(7L))),
                        IndexRange.create(3L, 3L),
                        Set.of());

        RollingAveragePoint point = averages.getFirst().points().getFirst();
        assertThat(point.includedIndexes()).containsExactlyInAnyOrder(1L, 3L);
        assertThat(point.rollingAverage())
                .isEqualTo(
                        Value.create(Fraction.create(new BigDecimal("30"), new BigDecimal("2"))));
    }

    @Test
    void continuesAfterTheLastMeasurementUntilTheWindowBecomesEmpty() {
        IndexedSeries series =
                IndexedSeries.create(List.of(IndexedValue.create(3L, Value.create(30))));

        RollingAverages averages =
                CALCULATOR.calculate(
                        series,
                        RollingWindows.create(List.of(RollingWindow.create(3L))),
                        IndexRange.create(1L, 6L),
                        Set.of());

        assertThat(averages.getFirst().points())
                .extracting(RollingAveragePoint::index)
                .containsExactly(3L, 4L, 5L);
    }

    @Test
    void calculatesEveryRequestedWindowInOrder() {
        IndexedSeries series =
                IndexedSeries.create(List.of(IndexedValue.create(1L, Value.create(10))));
        RollingWindows windows =
                RollingWindows.create(List.of(RollingWindow.create(1L), RollingWindow.create(7L)));

        RollingAverages averages =
                CALCULATOR.calculate(series, windows, IndexRange.create(1L, 1L), Set.of());

        assertThat(averages).extracting(RollingAverage::windowSize).containsExactly(1L, 7L);
    }

    @Test
    void acceptsNoRequestedWindows() {
        RollingAverages averages =
                CALCULATOR.calculate(
                        IndexedSeries.create(List.of()),
                        RollingWindows.create(List.of()),
                        IndexRange.create(1L, 1L),
                        Set.of());

        assertThat(averages).isEmpty();
    }

    @Test
    void rejectsNullInputs() {
        IndexedSeries series = IndexedSeries.create(List.of());
        RollingWindows windows = RollingWindows.create(List.of());
        IndexRange outputRange = IndexRange.create(1L, 1L);
        Set<CalculationRounding> roundings = Set.of();

        assertThatThrownBy(() -> CALCULATOR.calculate(null, windows, outputRange, roundings))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("indexed series must not be null");
        assertThatThrownBy(() -> CALCULATOR.calculate(series, null, outputRange, roundings))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("rolling windows must not be null");
        assertThatThrownBy(() -> CALCULATOR.calculate(series, windows, null, roundings))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("output range must not be null");
        assertThatThrownBy(() -> CALCULATOR.calculate(series, windows, outputRange, null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("roundings must not be null");
    }

    private static IndexedValue indexedValue(long index, String value) {
        return IndexedValue.create(index, Value.create(new BigDecimal(value)));
    }

    private static IndexedSeries sevenVaryingValues() {
        return IndexedSeries.create(
                List.of(
                        indexedValue(1L, "80.15"),
                        indexedValue(2L, "81.20"),
                        indexedValue(3L, "79.85"),
                        indexedValue(4L, "82.40"),
                        indexedValue(5L, "80.55"),
                        indexedValue(6L, "81.05"),
                        indexedValue(7L, "80.30")));
    }

    private static void assertPoint(
            RollingAveragePoint point,
            long expectedIndex,
            Set<Long> expectedIncludedIndexes,
            String expectedNumerator,
            String expectedDenominator,
            String expectedPrettyApproximation,
            String expectedPreciseApproximation) {
        assertThat(point.index()).isEqualTo(expectedIndex);
        assertThat(point.includedIndexes())
                .containsExactlyInAnyOrderElementsOf(expectedIncludedIndexes);
        assertThat(point.rollingAverage().exactValue())
                .isEqualTo(
                        Fraction.create(
                                new BigDecimal(expectedNumerator),
                                new BigDecimal(expectedDenominator)));
        assertThat(point.rollingAverage().approximations())
                .containsExactlyInAnyOrder(
                        Approximation.create(
                                new BigDecimal(expectedPrettyApproximation),
                                CalculationRounding.PRETTY),
                        Approximation.create(
                                new BigDecimal(expectedPreciseApproximation),
                                CalculationRounding.PRECISE));
    }
}
