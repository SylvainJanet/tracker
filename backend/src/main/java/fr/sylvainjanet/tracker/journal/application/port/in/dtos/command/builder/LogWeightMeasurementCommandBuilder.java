package fr.sylvainjanet.tracker.journal.application.port.in.dtos.command.builder;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.command.LogWeightMeasurementCommand;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class LogWeightMeasurementCommandBuilder {

    private LocalDate date;
    private BigDecimal weightInKg;

    private LogWeightMeasurementCommandBuilder() {}

    public static LogWeightMeasurementCommandBuilder aLogWeightMeasurementCommand() {
        return new LogWeightMeasurementCommandBuilder();
    }

    public LogWeightMeasurementCommandBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public LogWeightMeasurementCommandBuilder withWeightInKg(BigDecimal weightInKg) {
        this.weightInKg = weightInKg;
        return this;
    }

    public LogWeightMeasurementCommand build() {
        return new LogWeightMeasurementCommand(date, weightInKg);
    }
}
