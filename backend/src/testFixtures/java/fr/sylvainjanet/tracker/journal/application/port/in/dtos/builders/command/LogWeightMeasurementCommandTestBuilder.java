package fr.sylvainjanet.tracker.journal.application.port.in.dtos.builders.command;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.command.LogWeightMeasurementCommand;
import java.time.LocalDate;

public class LogWeightMeasurementCommandTestBuilder {
    private LocalDate date;
    private Float weightInKg;

    public static LogWeightMeasurementCommandTestBuilder aLogWeightMeasurementCommand() {
        return new LogWeightMeasurementCommandTestBuilder();
    }

    public LogWeightMeasurementCommandTestBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public LogWeightMeasurementCommandTestBuilder withWeightInKg(Float weightInKg) {
        this.weightInKg = weightInKg;
        return this;
    }

    public LogWeightMeasurementCommand build() {
        return new LogWeightMeasurementCommand(date, weightInKg);
    }
}
