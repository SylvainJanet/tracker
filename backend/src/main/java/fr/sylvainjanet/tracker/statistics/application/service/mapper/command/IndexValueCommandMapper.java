package fr.sylvainjanet.tracker.statistics.application.service.mapper.command;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.IndexedValueCommand;
import fr.sylvainjanet.tracker.statistics.domain.value.IndexedValue;
import fr.sylvainjanet.tracker.statistics.domain.value.Value;

public final class IndexValueCommandMapper {

    private IndexValueCommandMapper() {}

    public static IndexedValue indexedValue(IndexedValueCommand command) {
        return IndexedValue.create(command.index(), Value.create(command.value()));
    }
}
