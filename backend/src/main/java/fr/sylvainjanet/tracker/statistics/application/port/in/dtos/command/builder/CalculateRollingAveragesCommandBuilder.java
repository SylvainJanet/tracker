package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.builder;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.IndexedValueCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CalculateRollingAveragesCommandBuilder {

    private final List<IndexedValueCommand> values = new ArrayList<>();
    private final List<Long> windowSizes = new ArrayList<>();
    private long firstOutputIndex;
    private long lastOutputIndex;

    private CalculateRollingAveragesCommandBuilder() {}

    public static CalculateRollingAveragesCommandBuilder aCalculateRollingAveragesCommand() {
        return new CalculateRollingAveragesCommandBuilder();
    }

    public CalculateRollingAveragesCommandBuilder withValues(List<IndexedValueCommand> values) {
        this.values.addAll(Objects.requireNonNull(values, "values must not be null"));
        return this;
    }

    public CalculateRollingAveragesCommandBuilder withWindowSizes(List<Long> windowSizes) {
        this.windowSizes.addAll(
                Objects.requireNonNull(windowSizes, "window sizes must not be null"));
        return this;
    }

    public CalculateRollingAveragesCommandBuilder withFirstOutputIndex(long firstOutputIndex) {
        this.firstOutputIndex = firstOutputIndex;
        return this;
    }

    public CalculateRollingAveragesCommandBuilder withLastOutputIndex(long lastOutputIndex) {
        this.lastOutputIndex = lastOutputIndex;
        return this;
    }

    public CalculateRollingAveragesCommand build() {
        return new CalculateRollingAveragesCommand(
                values, windowSizes, firstOutputIndex, lastOutputIndex);
    }
}
