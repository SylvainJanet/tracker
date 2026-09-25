package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.builder;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.GetWeightAnalysisResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.GetWeightAnalysisResponse.DateRangeResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.GetWeightAnalysisResponse.RollingAveragePointResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.GetWeightAnalysisResponse.RollingAverageResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.GetWeightAnalysisResponse.WeightMeasurementResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class GetWeightAnalysisResponseBuilder {

    private LocalDate timelineStartDate;
    private DateRangeResponse range;
    private final List<WeightMeasurementResponse> weightMeasurements = new ArrayList<>();
    private final List<RollingAverageResponse> rollingAverages = new ArrayList<>();

    public static final class DateRangeResponseBuilder {

        private LocalDate startDate;
        private LocalDate endDate;

        private DateRangeResponseBuilder() {}

        public static DateRangeResponseBuilder aDateRangeResponse() {
            return new DateRangeResponseBuilder();
        }

        public DateRangeResponseBuilder withStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public DateRangeResponseBuilder withEndDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public DateRangeResponse build() {
            return new DateRangeResponse(startDate, endDate);
        }
    }

    public static final class WeightMeasurementResponseBuilder {

        private LocalDate date;
        private long dayNumber;
        private BigDecimal weightInKg;

        private WeightMeasurementResponseBuilder() {}

        public static WeightMeasurementResponseBuilder aWeightMeasurementResponse() {
            return new WeightMeasurementResponseBuilder();
        }

        public WeightMeasurementResponseBuilder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public WeightMeasurementResponseBuilder withDayNumber(long dayNumber) {
            this.dayNumber = dayNumber;
            return this;
        }

        public WeightMeasurementResponseBuilder withWeightInKg(BigDecimal weightInKg) {
            this.weightInKg = weightInKg;
            return this;
        }

        public WeightMeasurementResponse build() {
            return new WeightMeasurementResponse(date, dayNumber, weightInKg);
        }
    }

    public static final class RollingAverageResponseBuilder {

        private int windowInDays;
        private final List<RollingAveragePointResponse> points = new ArrayList<>();

        private RollingAverageResponseBuilder() {}

        public static RollingAverageResponseBuilder aRollingAverageResponse() {
            return new RollingAverageResponseBuilder();
        }

        public RollingAverageResponseBuilder withWindowInDays(int windowInDays) {
            this.windowInDays = windowInDays;
            return this;
        }

        public RollingAverageResponseBuilder withPoints(List<RollingAveragePointResponse> points) {
            this.points.addAll(points);
            return this;
        }

        public RollingAverageResponse build() {
            return new RollingAverageResponse(windowInDays, points);
        }
    }

    public static final class RollingAveragePointResponseBuilder {

        private LocalDate date;
        private long dayNumber;
        private BigDecimal averageWeightInKg;
        private int includedMeasurementCount;
        private boolean completeCalendarWindow;

        private RollingAveragePointResponseBuilder() {}

        public static RollingAveragePointResponseBuilder aRollingAveragePointResponse() {
            return new RollingAveragePointResponseBuilder();
        }

        public RollingAveragePointResponseBuilder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public RollingAveragePointResponseBuilder withDayNumber(long dayNumber) {
            this.dayNumber = dayNumber;
            return this;
        }

        public RollingAveragePointResponseBuilder withAverageWeightInKg(
                BigDecimal averageWeightInKg) {
            this.averageWeightInKg = averageWeightInKg;
            return this;
        }

        public RollingAveragePointResponseBuilder withIncludedMeasurementCount(
                int includedMeasurementCount) {
            this.includedMeasurementCount = includedMeasurementCount;
            return this;
        }

        public RollingAveragePointResponseBuilder withCompleteCalendarWindow(
                boolean completeCalendarWindow) {
            this.completeCalendarWindow = completeCalendarWindow;
            return this;
        }

        public RollingAveragePointResponse build() {
            return new RollingAveragePointResponse(
                    date,
                    dayNumber,
                    averageWeightInKg,
                    includedMeasurementCount,
                    completeCalendarWindow);
        }
    }

    private GetWeightAnalysisResponseBuilder() {}

    public static GetWeightAnalysisResponseBuilder aGetWeightAnalysisResponse() {
        return new GetWeightAnalysisResponseBuilder();
    }

    public GetWeightAnalysisResponseBuilder withTimelineStartDate(LocalDate timelineStartDate) {
        this.timelineStartDate = timelineStartDate;
        return this;
    }

    public GetWeightAnalysisResponseBuilder withRange(DateRangeResponse range) {
        this.range = range;
        return this;
    }

    public GetWeightAnalysisResponseBuilder withWeightMeasurements(
            List<WeightMeasurementResponse> weightMeasurements) {
        this.weightMeasurements.addAll(weightMeasurements);
        return this;
    }

    public GetWeightAnalysisResponseBuilder withRollingAverages(
            List<RollingAverageResponse> rollingAverages) {
        this.rollingAverages.addAll(rollingAverages);
        return this;
    }

    public GetWeightAnalysisResponse build() {
        return new GetWeightAnalysisResponse(
                timelineStartDate, range, weightMeasurements, rollingAverages);
    }
}
