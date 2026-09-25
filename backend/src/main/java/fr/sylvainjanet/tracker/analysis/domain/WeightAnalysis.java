package fr.sylvainjanet.tracker.analysis.domain;

import fr.sylvainjanet.tracker.shared.domain.DateRange;
import fr.sylvainjanet.tracker.shared.domain.WeightMeasurement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public final class WeightAnalysis {

    private final LocalDate timelineStartDate;
    private final DateRange range;
    private final List<WeightMeasurement> weightMeasurements;

    private WeightAnalysis(
            LocalDate timelineStartDate,
            DateRange range,
            List<WeightMeasurement> weightMeasurements) {
        this.timelineStartDate =
                Objects.requireNonNull(timelineStartDate, "timeline start date must not be null");
        this.range = Objects.requireNonNull(range, "represented range must not be null");
        Objects.requireNonNull(weightMeasurements, "weight measurements must not be null");

        if (timelineStartDate.isAfter(range.getStartDate())) {
            throw new IllegalArgumentException(
                    "timeline start date must not be after represented range start date");
        }

        validateMeasurements(weightMeasurements, range);
        this.weightMeasurements = List.copyOf(weightMeasurements);
    }

    public static WeightAnalysis create(
            LocalDate timelineStartDate,
            DateRange range,
            List<WeightMeasurement> weightMeasurements) {
        return new WeightAnalysis(timelineStartDate, range, weightMeasurements);
    }

    public LocalDate timelineStartDate() {
        return timelineStartDate;
    }

    public DateRange range() {
        return range;
    }

    public List<WeightMeasurement> weightMeasurements() {
        return weightMeasurements;
    }

    public long dayNumberFor(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");

        if (date.isBefore(timelineStartDate)) {
            throw new IllegalArgumentException("date must not be before the timeline start date");
        }

        return ChronoUnit.DAYS.between(timelineStartDate, date) + 1;
    }

    private static void validateMeasurements(
            List<WeightMeasurement> weightMeasurements, DateRange range) {
        LocalDate previousDate = null;

        for (WeightMeasurement measurement : weightMeasurements) {
            Objects.requireNonNull(measurement, "weight measurement must not be null");

            if (!range.contains(measurement.date())) {
                throw new IllegalArgumentException(
                        "weight measurement must be inside the represented range");
            }

            if (previousDate != null && !(measurement.date().isAfter(previousDate))) {
                throw new IllegalArgumentException(
                        "weight measurements must be ordered by unique ascending dates");
            }

            previousDate = measurement.date();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeightAnalysis that = (WeightAnalysis) o;
        return Objects.equals(timelineStartDate, that.timelineStartDate)
                && Objects.equals(range, that.range)
                && hasSameMeasurementInputsAs(that);
    }

    private boolean hasSameMeasurementInputsAs(WeightAnalysis that) {
        if (weightMeasurements.size() != that.weightMeasurements.size()) {
            return false;
        }

        for (int index = 0; index < weightMeasurements.size(); index++) {
            WeightMeasurement measurement = weightMeasurements.get(index);
            WeightMeasurement otherMeasurement = that.weightMeasurements.get(index);

            if (!Objects.equals(measurement.date(), otherMeasurement.date())
                    || !Objects.equals(measurement.weight(), otherMeasurement.weight())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(timelineStartDate, range);

        for (WeightMeasurement measurement : weightMeasurements) {
            result = 31 * result + Objects.hash(measurement.date(), measurement.weight());
        }

        return result;
    }
}
