package fr.sylvainjanet.tracker.tracking.domain;

import java.time.LocalDate;
import java.util.Objects;

public final class DailyRecord {

    private final LocalDate date;
    private final CompletionStatus status;

    private DailyRecord(LocalDate date, CompletionStatus status) {
        this.date = Objects.requireNonNull(date, "date must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    public static DailyRecord create(LocalDate date) {
        return new DailyRecord(date, CompletionStatus.IN_PROGRESS);
    }

    public static DailyRecord reconstitute(LocalDate date, CompletionStatus status) {
        return new DailyRecord(date, status);
    }

    public LocalDate date() {
        return date;
    }

    public CompletionStatus status() {
        return status;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return other instanceof DailyRecord that && date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }
}
