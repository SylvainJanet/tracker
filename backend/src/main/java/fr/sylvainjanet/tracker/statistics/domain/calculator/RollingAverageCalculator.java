package fr.sylvainjanet.tracker.statistics.domain.calculator;

import static java.util.stream.Collectors.toSet;

import fr.sylvainjanet.tracker.statistics.domain.value.CalculationRounding;
import fr.sylvainjanet.tracker.statistics.domain.value.ExactValue;
import fr.sylvainjanet.tracker.statistics.domain.value.IndexRange;
import fr.sylvainjanet.tracker.statistics.domain.value.IndexedSeries;
import fr.sylvainjanet.tracker.statistics.domain.value.IndexedValue;
import fr.sylvainjanet.tracker.statistics.domain.value.RollingAverage;
import fr.sylvainjanet.tracker.statistics.domain.value.RollingAveragePoint;
import fr.sylvainjanet.tracker.statistics.domain.value.RollingAverages;
import fr.sylvainjanet.tracker.statistics.domain.value.RollingWindow;
import fr.sylvainjanet.tracker.statistics.domain.value.RollingWindows;
import fr.sylvainjanet.tracker.statistics.domain.value.Value;
import fr.sylvainjanet.tracker.statistics.domain.value.ValueOrderedPair;
import fr.sylvainjanet.tracker.statistics.domain.value.Values;
import fr.sylvainjanet.tracker.technical.domain.MultiSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class RollingAverageCalculator {

    private static RollingAverageCalculator INSTANCE;
    private static final BasicArithmeticCalculator basicArithmeticCalculator =
            BasicArithmeticCalculator.getInstance();

    private RollingAverageCalculator() {}

    public static RollingAverageCalculator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RollingAverageCalculator();
        }
        return INSTANCE;
    }

    public RollingAverages calculate(
            IndexedSeries series,
            RollingWindows windows,
            IndexRange outputRange,
            Set<CalculationRounding> roundings) {
        Objects.requireNonNull(series, "indexed series must not be null");
        Objects.requireNonNull(windows, "rolling windows must not be null");
        Objects.requireNonNull(outputRange, "output range must not be null");
        Objects.requireNonNull(roundings, "roundings must not be null");

        return RollingAverages.create(
                windows.stream()
                        .map(window -> calculateWindow(series, window, outputRange, roundings))
                        .toList());
    }

    private static RollingAverage calculateWindow(
            IndexedSeries series,
            RollingWindow window,
            IndexRange outputRange,
            Set<CalculationRounding> roundings) {
        List<RollingAveragePoint> points = new ArrayList<>();

        for (long index = outputRange.start(); index <= outputRange.end(); index++) {
            Optional<RollingAveragePoint> point = calculatePoint(series, window, index, roundings);
            point.ifPresent(points::add);
        }

        return RollingAverage.create(window, points);
    }

    private static Optional<RollingAveragePoint> calculatePoint(
            IndexedSeries series,
            RollingWindow window,
            long outputIndex,
            Set<CalculationRounding> roundings) {

        long firstIncludedIndex = window.startFromEnd(outputIndex);

        List<IndexedValue> indexedValuesToAdd =
                series.stream()
                        .filter(
                                value ->
                                        value.index() >= firstIncludedIndex
                                                && value.index() <= outputIndex)
                        .toList();

        if (indexedValuesToAdd.isEmpty()) {
            return Optional.empty();
        }

        Set<Long> includedIndexes =
                indexedValuesToAdd.stream().map(IndexedValue::index).collect(toSet());
        MultiSet<ExactValue> exactValues =
                indexedValuesToAdd.stream()
                        .map(IndexedValue::value)
                        .map(Value::exactValue)
                        .collect(MultiSet.toMultiSet());
        Value numberOfValues = Value.create((indexedValuesToAdd.size()));

        Value sum = basicArithmeticCalculator.add(Values.createExactValues(exactValues));
        Value average =
                basicArithmeticCalculator.divide(
                        ValueOrderedPair.create(sum, numberOfValues), roundings);

        return Optional.of(RollingAveragePoint.create(outputIndex, includedIndexes, average));
    }
}
