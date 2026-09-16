package fr.sylvainjanet.tracker.tracking.adapter.out.persistence.exceptions;

public final class UnsupportedSqliteDailyRecordWeightException extends RuntimeException {

    public UnsupportedSqliteDailyRecordWeightException() {
        super("SQLite daily-record persistence supports only positive weights");
    }
}
