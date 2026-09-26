package fr.sylvainjanet.tracker.analysis.domain.builder;

import fr.sylvainjanet.tracker.analysis.domain.DatedSeries;
import fr.sylvainjanet.tracker.analysis.domain.DatedValue;
import fr.sylvainjanet.tracker.shared.domain.DateRange;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DatedSeriesBuilder {

    private LocalDate timelineStartDate;
    private LocalDate timelineEndDate;
    private DateRange range;
    private final List<DatedValue> values = new ArrayList<>();

    public static DatedSeriesBuilder aDatedSeries() {
        return new DatedSeriesBuilder();
    }

    public DatedSeriesBuilder withTimelineStartDate(LocalDate timelineStartDate) {
        this.timelineStartDate = timelineStartDate;
        return this;
    }

    public DatedSeriesBuilder withTimelineEndDate(LocalDate timelineEndDate) {
        this.timelineEndDate = timelineEndDate;
        return this;
    }

    public DatedSeriesBuilder withRange(DateRange range) {
        this.range = range;
        return this;
    }

    public DatedSeriesBuilder withValues(List<DatedValue> values) {
        this.values.addAll(Objects.requireNonNull(values, "values must not be null"));
        return this;
    }

    public DatedSeries build() {
        return DatedSeries.create(timelineStartDate, timelineEndDate, range, values);
    }
}
