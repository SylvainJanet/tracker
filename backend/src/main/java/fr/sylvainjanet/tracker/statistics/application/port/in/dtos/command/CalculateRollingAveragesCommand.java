package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command;

import java.util.List;

public record CalculateRollingAveragesCommand(
        List<IndexedValueCommand> values,
        List<Long> windowSizes,
        long firstOutputIndex,
        long lastOutputIndex) {}
