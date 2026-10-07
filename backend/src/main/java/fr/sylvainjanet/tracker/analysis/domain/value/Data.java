package fr.sylvainjanet.tracker.analysis.domain.value;

import static fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationError.genericError;

import fr.sylvainjanet.tracker.shared.domain.DateRange;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidator;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.generic.DomainGenericValueObject;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Data implements DomainGenericValueObject<Data> {

    private final DateRange dataCompleteRange;
    private final DateRange analysisRange;
    private final DatedSeries dataSeries;

    private Data(DateRange dataCompleteRange, DateRange analysisRange, DatedSeries dataSeries) {
        this.dataCompleteRange = dataCompleteRange;
        this.analysisRange = analysisRange;
        this.dataSeries = dataSeries;
        DomainValidator.validate(this);
    }

    public static Data create(
            DateRange dataCompleteRange, DateRange analysisRange, DatedSeries dataSeries) {
        return new Data(dataCompleteRange, analysisRange, dataSeries);
    }

    public static Data createComplete(
            DateRange completeDataAndAnalysisRange, List<DatedValue> values) {
        DatedSeries series = DatedSeries.create(values);
        return new Data(completeDataAndAnalysisRange, completeDataAndAnalysisRange, series);
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

        if (dataCompleteRange == null) {
            errors.add(genericError("dataCompleteRange must not be null"));
        }
        if (analysisRange == null) {
            errors.add(genericError("analysisRange must not be null"));
        }
        if (dataSeries == null) {
            errors.add(genericError("dataSeries must not be null"));
        }
        if (dataCompleteRange != null
                && analysisRange != null
                && !dataCompleteRange.contains(analysisRange)) {
            errors.add(genericError("analysisRange must be within dataCompleteRange"));
        }

        if (dataSeries != null && dataCompleteRange != null) {
            DateRange dataDateRange = dataSeries.dateRange();
            if (dataDateRange != null && !dataCompleteRange.contains(dataDateRange)) {
                errors.add(genericError("dataSeries date range must be within dataCompleteRange"));
            }
        }

        return errors;
    }

    public DateRange analysisRange() {
        return analysisRange;
    }

    public DatedSeries dataSeries() {
        return dataSeries;
    }

    public LocalDate timelineStartDate() {
        return dataCompleteRange.startDate();
    }

    private ChronoLocalDate timelineEndDate() {
        return dataCompleteRange.endDate();
    }

    public long indexFor(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");

        if (date.isBefore(timelineStartDate())) {
            throw new IllegalArgumentException("date must not be before the timeline start date");
        }

        if (date.isAfter(timelineEndDate())) {
            throw new IllegalArgumentException("date must not be after the timeline end date");
        }

        return ChronoUnit.DAYS.between(timelineStartDate(), date) + 1;
    }

    public LocalDate dateForIndex(long index) {
        if (index < 1) {
            throw new IllegalArgumentException("index must be greater than or equal to 1");
        }

        LocalDate date = timelineStartDate().plusDays(index - 1);

        if (date.isAfter(timelineEndDate())) {
            throw new IllegalArgumentException("index must not be after the timeline end date");
        }

        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Data data1 = (Data) o;
        return Objects.equals(dataCompleteRange, data1.dataCompleteRange)
                && Objects.equals(analysisRange, data1.analysisRange)
                && Objects.equals(dataSeries, data1.dataSeries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataCompleteRange, analysisRange, dataSeries);
    }

    @Override
    public String toString() {
        return "Data{"
                + "dataCompleteRange="
                + dataCompleteRange
                + ", analysisRange="
                + analysisRange
                + ", dataSeries="
                + dataSeries
                + '}';
    }
}
