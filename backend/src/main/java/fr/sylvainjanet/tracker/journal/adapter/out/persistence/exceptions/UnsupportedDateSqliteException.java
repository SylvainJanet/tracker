package fr.sylvainjanet.tracker.journal.adapter.out.persistence.exceptions;

public final class UnsupportedDateSqliteException extends WeightMeasurementSqliteException {

    public UnsupportedDateSqliteException() {
        super("SQLite date persistence supports only four-digit years");
    }
}
