package fr.sylvainjanet.tracker.analysis.application.service.mapper.command;

import static fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.builder.CalculateRollingAveragesCommandBuilder.aCalculateRollingAveragesCommand;

import fr.sylvainjanet.tracker.analysis.domain.value.DatedSeries;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.IndexedValueCommand;
import java.util.List;

public final class CalculateRollingAveragesCommandMapper {

    private CalculateRollingAveragesCommandMapper() {}

    public static CalculateRollingAveragesCommand calculateCommand(
            DatedSeries series, List<Long> windowSizes) {

        List<IndexedValueCommand> values =
                series.values().stream()
                        .map(
                                datedValue ->
                                        IndexedValueCommandMapper.indexedValueCommand(
                                                series, datedValue))
                        .toList();

        return aCalculateRollingAveragesCommand()
                .withValues(values)
                .withWindowSizes(windowSizes)
                .withFirstOutputIndex(series.indexFor(series.range().startDate()))
                .withLastOutputIndex(series.indexFor(series.range().endDate()))
                .build();
    }
}
