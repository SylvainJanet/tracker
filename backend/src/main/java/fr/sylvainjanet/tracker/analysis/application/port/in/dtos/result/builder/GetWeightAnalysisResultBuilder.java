package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.DateRangeResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.WeightMeasurementResult;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class GetWeightAnalysisResultBuilder {

    private LocalDate timelineStartDate;
    private DateRangeResult range;
    private List<WeightMeasurementResult> weightMeasurements;

    public static final class DateRangeResultBuilder {

        private LocalDate startDate;
        private LocalDate endDate;

        private DateRangeResultBuilder() {}

        public static DateRangeResultBuilder aDateRangeResult() {
            return new DateRangeResultBuilder();
        }

        public DateRangeResultBuilder withStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public DateRangeResultBuilder withEndDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public DateRangeResult build() {
            return new DateRangeResult(startDate, endDate);
        }
    }

    public static final class WeightMeasurementResultBuilder {

        private LocalDate date;
        private long dayNumber;
        private BigDecimal weightInKg;

        private WeightMeasurementResultBuilder() {}

        public static WeightMeasurementResultBuilder aWeightMeasurementResult() {
            return new WeightMeasurementResultBuilder();
        }

        public WeightMeasurementResultBuilder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public WeightMeasurementResultBuilder withDayNumber(long dayNumber) {
            this.dayNumber = dayNumber;
            return this;
        }

        public WeightMeasurementResultBuilder withWeightInKg(BigDecimal weightInKg) {
            this.weightInKg = weightInKg;
            return this;
        }

        public WeightMeasurementResult build() {
            return new WeightMeasurementResult(date, dayNumber, weightInKg);
        }
    }

    private GetWeightAnalysisResultBuilder() {}

    public static GetWeightAnalysisResultBuilder aGetWeightAnalysisResult() {
        return new GetWeightAnalysisResultBuilder();
    }

    public GetWeightAnalysisResultBuilder withTimelineStartDate(LocalDate timelineStartDate) {
        this.timelineStartDate = timelineStartDate;
        return this;
    }

    public GetWeightAnalysisResultBuilder withRange(DateRangeResult range) {
        this.range = range;
        return this;
    }

    public GetWeightAnalysisResultBuilder withWeightMeasurements(
            List<WeightMeasurementResult> weightMeasurements) {
        this.weightMeasurements = weightMeasurements;
        return this;
    }

    public GetWeightAnalysisResult build() {
        return new GetWeightAnalysisResult(timelineStartDate, range, weightMeasurements);
    }
}
