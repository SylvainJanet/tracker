package fr.sylvainjanet.tracker.tracking.application.port.out.gateway.store;

import fr.sylvainjanet.tracker.tracking.application.port.out.dtos.outcome.DailyRecordCreationOutcome;
import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;

public interface DailyRecordStore {

    DailyRecordCreationOutcome create(DailyRecord instruction);
}
