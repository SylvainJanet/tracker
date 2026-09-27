package fr.sylvainjanet.tracker.statistics.application.service.mapper.command;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand;
import fr.sylvainjanet.tracker.statistics.domain.value.IndexRange;
import fr.sylvainjanet.tracker.statistics.domain.value.IndexedValue;
import fr.sylvainjanet.tracker.statistics.domain.value.RollingWindow;
import java.util.List;
import java.util.Optional;

public final class CalculateRollingAveragesCommandMapper {

    private CalculateRollingAveragesCommandMapper() {}

    public static List<IndexedValue> indexedValues(CalculateRollingAveragesCommand command) {
        return command.values().stream().map(IndexValueCommandMapper::indexedValue).toList();
    }

    public static List<RollingWindow> rollingWindows(CalculateRollingAveragesCommand command) {
        return command.windowSizes().stream().map(RollingWindow::create).toList();
    }

    public static Optional<IndexRange> indexRange(CalculateRollingAveragesCommand command) {
        if (command.values().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(
                IndexRange.create(command.firstOutputIndex(), command.lastOutputIndex()));
    }
}
