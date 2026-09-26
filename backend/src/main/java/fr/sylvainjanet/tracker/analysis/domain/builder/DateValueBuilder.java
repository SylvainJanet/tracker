package fr.sylvainjanet.tracker.analysis.domain.builder;

import fr.sylvainjanet.tracker.analysis.domain.DatedValue;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DateValueBuilder {

    private LocalDate date;
    private BigDecimal value;

    public static DateValueBuilder aDateValue() {
        return new DateValueBuilder();
    }

    public DateValueBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public DateValueBuilder withValue(BigDecimal value) {
        this.value = value;
        return this;
    }

    public DatedValue build() {
        return new DatedValue(date, value);
    }
}
