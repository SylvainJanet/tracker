package fr.sylvainjanet.tracker.tracking.adapter.out.persistence;

import fr.sylvainjanet.tracker.tracking.application.port.out.dtos.outcome.DailyRecordCreationOutcome;
import fr.sylvainjanet.tracker.tracking.application.port.out.gateway.store.DailyRecordStore;
import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public final class InMemoryDailyRecordStore implements DailyRecordStore {

    private final Map<LocalDate, DailyRecord> records = new HashMap<>();

    @Override
    public DailyRecordCreationOutcome create(DailyRecord instruction) {
        DailyRecord existingRecord = records.putIfAbsent(instruction.date(), instruction);

        return existingRecord == null
                ? DailyRecordCreationOutcome.CREATED
                : DailyRecordCreationOutcome.ALREADY_EXISTS;
    }

    public void clear() {
        records.clear();
    }
}
