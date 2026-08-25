package fr.sylvainjanet.tracker.tracking.adapter.out.persistence.exceptions;

public final class UnsupportedSqliteDailyRecordDateException extends RuntimeException {

    public UnsupportedSqliteDailyRecordDateException() {
        super("SQLite daily-record persistence supports only four-digit years");
    }
}
