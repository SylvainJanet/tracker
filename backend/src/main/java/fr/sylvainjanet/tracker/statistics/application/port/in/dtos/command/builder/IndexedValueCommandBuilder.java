package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.builder;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.IndexedValueCommand;
import java.math.BigDecimal;

public final class IndexedValueCommandBuilder {

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
