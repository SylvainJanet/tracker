package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record CalculateRollingAveragesCommand(
        List<IndexedValueCommand> values, List<Integer> windowSizes) {

    public CalculateRollingAveragesCommand {
        values = List.copyOf(Objects.requireNonNull(values, "values must not be null"));
        windowSizes =
                List.copyOf(Objects.requireNonNull(windowSizes, "window sizes must not be null"));
    }

    public record IndexedValueCommand(long index, BigDecimal value) {

        public IndexedValueCommand {
            Objects.requireNonNull(value, "value must not be null");
        }
    }
}
