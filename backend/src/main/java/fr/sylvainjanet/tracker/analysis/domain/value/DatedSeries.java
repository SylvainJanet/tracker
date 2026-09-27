package fr.sylvainjanet.tracker.analysis.domain.value;

import static fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationError.genericError;
import static java.util.Collections.unmodifiableList;

import fr.sylvainjanet.tracker.shared.domain.DateRange;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidator;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.generic.DomainGenericValueObject;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class DatedSeries implements DomainGenericValueObject<DatedSeries> {

    private final LocalDate timelineStartDate;
    private final LocalDate timelineEndDate;
    private final DateRange range;
    private final List<DatedValue> values;

    private DatedSeries(
            LocalDate timelineStartDate,
            LocalDate timelineEndDate,
            DateRange range,
            List<DatedValue> values) {
        this.timelineStartDate = timelineStartDate;
        this.timelineEndDate = timelineEndDate;
        this.range = range;
        this.values = values == null ? null : unmodifiableList(new ArrayList<>(values));
        DomainValidator.validate(this);
    }

    @Override
    public Set<
                    DomainValidationError<
                            GenericDomainValidationErrorKind, GenericDomainValidationErrorMessage>>
            validate() {
        Set<
                        DomainValidationError<
                                GenericDomainValidationErrorKind,
                                GenericDomainValidationErrorMessage>>
                errors = new HashSet<>();

        if (timelineStartDate == null) {
            errors.add(genericError("timeline start date must not be null"));
        }
        if (timelineEndDate == null) {
            errors.add(genericError("timeline end date must not be null"));
        }
        if (range == null) {
            errors.add(genericError("represented dateRange must not be null"));
        }
        if (values == null) {
            errors.add(genericError("values must not be null"));
        }
        if (timelineStartDate != null
                && timelineEndDate != null
                && timelineStartDate.isAfter(timelineEndDate)) {
            errors.add(genericError("timeline start date must not be after timeline end date"));
        }
        if (range != null
                && timelineStartDate != null
                && range.startDate().isBefore(timelineStartDate)) {
            errors.add(
                    genericError(
                            "represented dateRange must not start before the timeline start date"));
        }
        if (range != null && timelineEndDate != null && range.endDate().isAfter(timelineEndDate)) {
            errors.add(
                    genericError("represented dateRange must not end after the timeline end date"));
        }

        if (values == null) {
            return errors;
        }

        LocalDate previousDate = null;
        for (DatedValue value : values) {
            if (value == null) {
                errors.add(genericError("value must not be null"));
                continue;
            }
            if (range != null && !range.contains(value.date())) {
                errors.add(genericError("value must be inside the represented dateRange"));
            }
            if (previousDate != null && !value.date().isAfter(previousDate)) {
                errors.add(genericError("values must be ordered by unique ascending dates"));
            }

            previousDate = value.date();
        }

        return errors;
    }

    public static DatedSeries create(
            LocalDate timelineStartDate,
            LocalDate timelineEndDate,
            DateRange range,
            List<DatedValue> values) {
        return new DatedSeries(timelineStartDate, timelineEndDate, range, values);
    }

    public static DatedSeries completeSeries(DateRange range, List<DatedValue> values) {
        return new DatedSeries(range.startDate(), range.endDate(), range, values);
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

    public LocalDate dateForIndex(long index) {
        if (index < 1) {
            throw new IllegalArgumentException("index must be greater than or equal to 1");
        }

        LocalDate date = timelineStartDate.plusDays(index - 1);

        if (date.isAfter(timelineEndDate)) {
            throw new IllegalArgumentException("index must not be after the timeline end date");
        }

        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DatedSeries that = (DatedSeries) o;
        return Objects.equals(timelineStartDate, that.timelineStartDate)
                && Objects.equals(timelineEndDate, that.timelineEndDate)
                && Objects.equals(range, that.range)
                && Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timelineStartDate, timelineEndDate, range, values);
    }

    @Override
    public String toString() {
        return "DatedSeries{"
                + "timelineStartDate="
                + timelineStartDate
                + ", timelineEndDate="
                + timelineEndDate
                + ", range="
                + range
                + ", values="
                + values
                + '}';
    }
}
