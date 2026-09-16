package fr.sylvainjanet.tracker.tracking.domain.builders;

import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;
import fr.sylvainjanet.tracker.tracking.domain.Weight;
import java.time.LocalDate;

public final class DailyRecordTestBuilder {
    private LocalDate date;
    private Weight weight;

    public static DailyRecordTestBuilder aDailyRecord() {
        return new DailyRecordTestBuilder();
    }

    public DailyRecordTestBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public DailyRecordTestBuilder withWeightInKg(Float weightInKg) {
        this.weight = Weight.of(weightInKg);
        return this;
    }

    public DailyRecord build() {
        return DailyRecord.create(date, weight);
    }
}
