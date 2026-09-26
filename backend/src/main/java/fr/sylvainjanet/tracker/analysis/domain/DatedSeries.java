package fr.sylvainjanet.tracker.analysis.domain;

import fr.sylvainjanet.tracker.shared.domain.DateRange;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public final class DatedSeries {

    private final LocalDate timelineStartDate;
    private final LocalDate timelineEndDate;
    private final DateRange range;
    private final List<DatedValue> values;

    private DatedSeries(
            LocalDate timelineStartDate,
            LocalDate timelineEndDate,
            DateRange range,
            List<DatedValue> values) {
        this.timelineStartDate =
                Objects.requireNonNull(timelineStartDate, "timeline start date must not be null");
        this.timelineEndDate =
                Objects.requireNonNull(timelineEndDate, "timeline end date must not be null");
        this.range = Objects.requireNonNull(range, "represented range must not be null");
        Objects.requireNonNull(values, "values must not be null");

        if (timelineStartDate.isAfter(timelineEndDate)) {
            throw new IllegalArgumentException(
                    "timeline start date must not be after timeline end date");
        }

        if (range.getStartDate().isBefore(timelineStartDate)) {
            throw new IllegalArgumentException(
                    "represented range must not start before the timeline start date");
        }

        if (range.getEndDate().isAfter(timelineEndDate)) {
            throw new IllegalArgumentException(
                    "represented range must not end after the timeline end date");
        }

        validateValues(values, range);
        this.values = List.copyOf(values);
    }

    public static DatedSeries create(
            LocalDate timelineStartDate,
            LocalDate timelineEndDate,
            DateRange range,
            List<DatedValue> values) {
        return new DatedSeries(timelineStartDate, timelineEndDate, range, values);
    }

    public LocalDate timelineStartDate() {
        return timelineStartDate;
    }

    public LocalDate timelineEndDate() {
        return timelineEndDate;
    }

    public DateRange range() {
        return range;
    }

    public List<DatedValue> values() {
        return values;
    }

    public long indexFor(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");

        if (date.isBefore(timelineStartDate)) {
            throw new IllegalArgumentException("date must not be before the timeline start date");
        }

        if (date.isAfter(timelineEndDate)) {
            throw new IllegalArgumentException("date must not be after the timeline end date");
        }

        return ChronoUnit.DAYS.between(timelineStartDate, date) + 1;
    }

    private static void validateValues(List<DatedValue> values, DateRange range) {
        LocalDate previousDate = null;

        for (DatedValue value : values) {
            Objects.requireNonNull(value, "value must not be null");

            if (!range.contains(value.date())) {
                throw new IllegalArgumentException("value must be inside the represented range");
            }

            if (previousDate != null && !value.date().isAfter(previousDate)) {
                throw new IllegalArgumentException(
                        "values must be ordered by unique ascending dates");
            }

            previousDate = value.date();
        }
    }
}
