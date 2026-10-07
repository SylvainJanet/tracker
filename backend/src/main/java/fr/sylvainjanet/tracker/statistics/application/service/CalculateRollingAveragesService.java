package fr.sylvainjanet.tracker.statistics.application.service;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.usecase.CalculateRollingAveragesUseCase;
import fr.sylvainjanet.tracker.statistics.application.service.mapper.command.CalculateRollingAveragesCommandMapper;
import fr.sylvainjanet.tracker.statistics.application.service.mapper.result.CalculateRollingAveragesResultMapper;
import fr.sylvainjanet.tracker.statistics.domain.calculator.RollingAverageCalculator;
import fr.sylvainjanet.tracker.statistics.domain.processor.IndexSeriesSorter;
import fr.sylvainjanet.tracker.statistics.domain.processor.WindowsSorter;
import fr.sylvainjanet.tracker.statistics.domain.value.CalculationRounding;
import fr.sylvainjanet.tracker.statistics.domain.value.IndexRange;
import fr.sylvainjanet.tracker.statistics.domain.value.IndexedSeries;
import fr.sylvainjanet.tracker.statistics.domain.value.IndexedValue;
import fr.sylvainjanet.tracker.statistics.domain.value.RollingAverages;
import fr.sylvainjanet.tracker.statistics.domain.value.RollingWindow;
import fr.sylvainjanet.tracker.statistics.domain.value.RollingWindows;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class CalculateRollingAveragesService implements CalculateRollingAveragesUseCase {

    private final RollingAverageCalculator calculator = RollingAverageCalculator.getInstance();
    private final IndexSeriesSorter indexSeriesSorter = IndexSeriesSorter.getInstance();
    private final WindowsSorter windowsSorter = WindowsSorter.getInstance();

    @Override
    public CalculateRollingAveragesResult calculate(CalculateRollingAveragesCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        List<IndexedValue> indexedValues =
                CalculateRollingAveragesCommandMapper.indexedValues(command);
        List<RollingWindow> rollingWindows =
                CalculateRollingAveragesCommandMapper.rollingWindows(command);
        Optional<IndexRange> indexRange = CalculateRollingAveragesCommandMapper.indexRange(command);

        if (indexRange.isEmpty()) {
            return CalculateRollingAveragesResult.empty();
        }
        Set<CalculationRounding> roundings = CalculationRounding.all();

        IndexedSeries indexedSeries =
                IndexedSeries.create(indexSeriesSorter.sortByIndex(indexedValues));
        RollingWindows windows = RollingWindows.create(windowsSorter.sortByIndex(rollingWindows));

        RollingAverages calculation =
                calculator.calculate(indexedSeries, windows, indexRange.get(), roundings);

        return CalculateRollingAveragesResultMapper.calculateRollingAveragesResult(calculation);
    }
}
