package fr.sylvainjanet.tracker.shared.domain;

import java.time.LocalDate;
import java.util.Objects;

public class DateRange {

    private final LocalDate startDate;
    private final LocalDate endDate;

    private DateRange(LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, "start date must not be null");
        Objects.requireNonNull(endDate, "end date must not be null");

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("start date must not be after end date");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static DateRange of(LocalDate startDate, LocalDate endDate) {
        return new DateRange(startDate, endDate);
    }

    public boolean contains(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        DateRange dateRange = (DateRange) o;
        return startDate.equals(dateRange.startDate) && endDate.equals(dateRange.endDate);
    }

    @Override
    public int hashCode() {
        int result = startDate.hashCode();
        result = 31 * result + endDate.hashCode();
        return result;
    }
}
