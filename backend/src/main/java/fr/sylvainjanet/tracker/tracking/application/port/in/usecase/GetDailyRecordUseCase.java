package fr.sylvainjanet.tracker.tracking.application.port.in.usecase;

import fr.sylvainjanet.tracker.tracking.application.port.in.exceptions.DailyRecordNotFoundException;
import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;
import java.time.LocalDate;

public interface GetDailyRecordUseCase {

    DailyRecord execute(LocalDate query) throws DailyRecordNotFoundException;
}
