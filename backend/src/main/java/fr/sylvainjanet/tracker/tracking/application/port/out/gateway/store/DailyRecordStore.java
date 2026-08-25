package fr.sylvainjanet.tracker.tracking.application.port.out.gateway.store;

import fr.sylvainjanet.tracker.tracking.application.port.out.dtos.outcome.DailyRecordCreationOutcome;
import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;
import java.time.LocalDate;
import java.util.Optional;

public interface DailyRecordStore {

    Optional<DailyRecord> findByDate(LocalDate criteria);

    DailyRecordCreationOutcome create(DailyRecord instruction);
}
