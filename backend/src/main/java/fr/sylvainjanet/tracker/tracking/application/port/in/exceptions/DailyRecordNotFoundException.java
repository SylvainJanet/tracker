package fr.sylvainjanet.tracker.tracking.application.port.in.exceptions;

import java.time.LocalDate;

public final class DailyRecordNotFoundException extends DailyRecordApplicationException {

    public DailyRecordNotFoundException(LocalDate date) {
        super("No daily record exists for " + date);
    }
}
