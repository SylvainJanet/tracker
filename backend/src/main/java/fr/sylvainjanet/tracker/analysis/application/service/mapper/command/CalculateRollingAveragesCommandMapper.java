package fr.sylvainjanet.tracker.analysis.application.service.mapper.command;

import static fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.builder.CalculateRollingAveragesCommandBuilder.aCalculateRollingAveragesCommand;

import fr.sylvainjanet.tracker.analysis.domain.value.Data;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.IndexedValueCommand;
import java.util.List;

public final class CalculateRollingAveragesCommandMapper {

    private CalculateRollingAveragesCommandMapper() {}

    public static CalculateRollingAveragesCommand calculateCommand(
            Data data, List<Long> windowSizes) {

        List<IndexedValueCommand> values =
                data.dataSeries().stream()
                        .map(
                                datedValue ->
                                        IndexedValueCommandMapper.indexedValueCommand(
                                                data, datedValue))
                        .toList();

        return aCalculateRollingAveragesCommand()
                .withValues(values)
                .withWindowSizes(windowSizes)
                .withFirstOutputIndex(data.indexFor(data.analysisRange().startDate()))
                .withLastOutputIndex(data.indexFor(data.analysisRange().endDate()))
                .build();
    }
}
