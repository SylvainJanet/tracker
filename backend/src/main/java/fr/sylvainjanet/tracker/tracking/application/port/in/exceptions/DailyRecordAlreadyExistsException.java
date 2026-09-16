package fr.sylvainjanet.tracker.tracking.application.port.in.exceptions;

import java.time.LocalDate;

public final class DailyRecordAlreadyExistsException extends DailyRecordApplicationException {

    private final LocalDate date;

    public DailyRecordAlreadyExistsException(LocalDate date) {
        super("A daily record already exists for: " + date);
        this.date = date;
    }

    public LocalDate date() {
        return date;
    }
}
