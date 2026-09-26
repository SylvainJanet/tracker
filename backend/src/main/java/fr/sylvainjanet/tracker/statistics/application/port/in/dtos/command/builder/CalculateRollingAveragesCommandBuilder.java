package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.builder;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand.IndexedValueCommand;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CalculateRollingAveragesCommandBuilder {

    private final List<IndexedValueCommand> values = new ArrayList<>();
    private final List<Integer> windowSizes = new ArrayList<>();

    private CalculateRollingAveragesCommandBuilder() {}

    public static final class IndexedValueCommandBuilder {

        private long index;
        private BigDecimal value;

        private IndexedValueCommandBuilder() {}

        public static IndexedValueCommandBuilder anIndexedValueCommand() {
            return new IndexedValueCommandBuilder();
        }

        public IndexedValueCommandBuilder withIndex(long index) {
            this.index = index;
            return this;
        }

        public IndexedValueCommandBuilder withValue(BigDecimal value) {
            this.value = value;
            return this;
        }

        public IndexedValueCommand build() {
            return new IndexedValueCommand(index, value);
        }
    }

    public static CalculateRollingAveragesCommandBuilder aCalculateRollingAveragesCommand() {
        return new CalculateRollingAveragesCommandBuilder();
    }

    public CalculateRollingAveragesCommandBuilder withValues(List<IndexedValueCommand> values) {
        this.values.addAll(Objects.requireNonNull(values, "values must not be null"));
        return this;
    }

    public CalculateRollingAveragesCommandBuilder withWindowSizes(List<Integer> windowSizes) {
        this.windowSizes.addAll(
                Objects.requireNonNull(windowSizes, "window sizes must not be null"));
        return this;
    }

    public CalculateRollingAveragesCommand build() {
        return new CalculateRollingAveragesCommand(values, windowSizes);
    }
}
