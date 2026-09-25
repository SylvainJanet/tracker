package fr.sylvainjanet.tracker.shared.domain.builder;

import fr.sylvainjanet.tracker.shared.domain.DateRange;
import java.time.LocalDate;

public class DateRangeBuilder {

    private LocalDate startDate;
    private LocalDate endDate;

    public static DateRangeBuilder aDateRange() {
        return new DateRangeBuilder();
    }

    public DateRangeBuilder withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public DateRangeBuilder withEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public DateRange build() {
        return DateRange.of(startDate, endDate);
    }
}
