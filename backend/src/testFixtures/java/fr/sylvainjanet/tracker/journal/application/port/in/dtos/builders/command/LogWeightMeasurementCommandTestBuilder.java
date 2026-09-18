package fr.sylvainjanet.tracker.journal.application.port.in.dtos.builders.command;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.command.LogWeightMeasurementCommand;
import java.math.BigDecimal;
import java.time.LocalDate;

public class LogWeightMeasurementCommandTestBuilder {
    private LocalDate date;
    private BigDecimal weightInKg;

    public static LogWeightMeasurementCommandTestBuilder aLogWeightMeasurementCommand() {
        return new LogWeightMeasurementCommandTestBuilder();
    }

    public LogWeightMeasurementCommandTestBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public LogWeightMeasurementCommandTestBuilder withWeightInKg(BigDecimal weightInKg) {
        this.weightInKg = weightInKg;
        return this;
    }

    public LogWeightMeasurementCommand build() {
        return new LogWeightMeasurementCommand(date, weightInKg);
    }
}
