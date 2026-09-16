package fr.sylvainjanet.tracker.tracking.application.port.in.usecase;

import fr.sylvainjanet.tracker.tracking.application.port.in.exceptions.DailyRecordAlreadyExistsException;
import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;

public interface CreateDailyRecordUseCase {

    DailyRecord execute(DailyRecord command) throws DailyRecordAlreadyExistsException;
}
