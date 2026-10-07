package fr.sylvainjanet.tracker.analysis.application.service.mapper.command;

import static fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.builder.IndexedValueCommandBuilder.anIndexedValueCommand;

import fr.sylvainjanet.tracker.analysis.domain.value.Data;
import fr.sylvainjanet.tracker.analysis.domain.value.DatedValue;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.IndexedValueCommand;

public final class IndexedValueCommandMapper {

    private IndexedValueCommandMapper() {}

    public static IndexedValueCommand indexedValueCommand(Data data, DatedValue value) {
        return anIndexedValueCommand()
                .withIndex(data.indexFor(value.date()))
                .withValue(value.value())
                .build();
    }
}
