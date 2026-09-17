package fr.sylvainjanet.tracker.journal.adapter.out.persistence.exceptions;

public final class UnsupportedWeightSqliteException extends WeightMeasurementSqliteException {

    public UnsupportedWeightSqliteException() {
        super("SQLite weight persistence supports only positive weights");
    }
}
