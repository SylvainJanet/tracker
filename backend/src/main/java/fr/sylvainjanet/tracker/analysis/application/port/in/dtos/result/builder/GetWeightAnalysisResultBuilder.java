package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.AnalysisDateRangeResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.AnalysisWeightRollingAverageResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.AnalysisWeightValueResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.WeightRollingAveragePointResult;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GetWeightAnalysisResultBuilder {

    private LocalDate timelineStartDate;
    private AnalysisDateRangeResult dateRange;
    private final List<AnalysisWeightValueResult> weightValues = new ArrayList<>();
    private final List<AnalysisWeightRollingAverageResult> rollingAverages = new ArrayList<>();

    public static final class AnalysisDateRangeResultBuilder {

        private LocalDate startDate;
        private LocalDate endDate;

        private AnalysisDateRangeResultBuilder() {}

        public static AnalysisDateRangeResultBuilder anAnalysisDateRangeResult() {
            return new AnalysisDateRangeResultBuilder();
        }

        public AnalysisDateRangeResultBuilder withStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public AnalysisDateRangeResultBuilder withEndDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public AnalysisDateRangeResult build() {
            return new AnalysisDateRangeResult(startDate, endDate);
        }
    }

    public static final class AnalysisWeightValueResultBuilder {

        private LocalDate date;
        private long dayNumber;
        private BigDecimal weightInKg;

        private AnalysisWeightValueResultBuilder() {}

        public static AnalysisWeightValueResultBuilder anAnalysisWeightValueResult() {
            return new AnalysisWeightValueResultBuilder();
        }

        public AnalysisWeightValueResultBuilder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public AnalysisWeightValueResultBuilder withDayNumber(long dayNumber) {
            this.dayNumber = dayNumber;
            return this;
        }

        public AnalysisWeightValueResultBuilder withWeightInKg(BigDecimal weightInKg) {
            this.weightInKg = weightInKg;
            return this;
        }

        public AnalysisWeightValueResult build() {
            return new AnalysisWeightValueResult(date, dayNumber, weightInKg);
        }
    }

    public static final class AnalysisWeightRollingAverageResultBuilder {

        private int windowInDays;
        private final List<WeightRollingAveragePointResult> points = new ArrayList<>();

        private AnalysisWeightRollingAverageResultBuilder() {}

        public static AnalysisWeightRollingAverageResultBuilder
                anAnalysisWeightRollingAverageResult() {
            return new AnalysisWeightRollingAverageResultBuilder();
        }

        public AnalysisWeightRollingAverageResultBuilder withWindowInDays(int windowInDays) {
            this.windowInDays = windowInDays;
            return this;
        }

        public AnalysisWeightRollingAverageResultBuilder withPoints(
                List<WeightRollingAveragePointResult> points) {
            this.points.addAll(Objects.requireNonNull(points, "points must not be null"));
            return this;
        }

        public AnalysisWeightRollingAverageResult build() {
            return new AnalysisWeightRollingAverageResult(windowInDays, points);
        }
    }

    public static final class WeightRollingAveragePointResultBuilder {

        private LocalDate date;
        private long dayNumber;
        private BigDecimal averageWeightInKg;

        private WeightRollingAveragePointResultBuilder() {}

        public static WeightRollingAveragePointResultBuilder aWeightRollingAveragePointResult() {
            return new WeightRollingAveragePointResultBuilder();
        }

        public WeightRollingAveragePointResultBuilder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public WeightRollingAveragePointResultBuilder withDayNumber(long dayNumber) {
            this.dayNumber = dayNumber;
            return this;
        }

        public WeightRollingAveragePointResultBuilder withAverageWeightInKg(
                BigDecimal averageWeightInKg) {
            this.averageWeightInKg = averageWeightInKg;
            return this;
        }

        public WeightRollingAveragePointResult build() {
            return new WeightRollingAveragePointResult(date, dayNumber, averageWeightInKg);
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

    public GetWeightAnalysisResultBuilder withDateRange(AnalysisDateRangeResult dateRange) {
        this.dateRange = dateRange;
        return this;
    }

    public GetWeightAnalysisResultBuilder withWeightValues(
            List<AnalysisWeightValueResult> weightValues) {
        this.weightValues.addAll(
                Objects.requireNonNull(weightValues, "weight values must not be null"));
        return this;
    }

    public GetWeightAnalysisResultBuilder withRollingAverages(
            List<AnalysisWeightRollingAverageResult> rollingAverages) {
        this.rollingAverages.addAll(
                Objects.requireNonNull(rollingAverages, "rolling averages must not be null"));
        return this;
    }

    public GetWeightAnalysisResult build() {
        return new GetWeightAnalysisResult(
                timelineStartDate, dateRange, weightValues, rollingAverages);
    }
}
