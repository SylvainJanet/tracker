package fr.sylvainjanet.tracker.tracking.application.port.in.usecase;

import fr.sylvainjanet.tracker.tracking.application.port.in.exceptions.DailyRecordAlreadyExistsException;
import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;
import java.time.LocalDate;

public interface CreateDailyRecordUseCase {

    DailyRecord execute(LocalDate command) throws DailyRecordAlreadyExistsException;
}
