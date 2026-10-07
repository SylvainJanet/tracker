package fr.sylvainjanet.tracker.journal.application.port.out.dtos.instruction.builder;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.instruction.LogWeightMeasurementInstruction;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class LogWeightMeasurementInstructionBuilder {

    private LocalDate date;
    private BigDecimal weightInKg;

    private LogWeightMeasurementInstructionBuilder() {}

    public static LogWeightMeasurementInstructionBuilder aLogWeightMeasurementInstruction() {
        return new LogWeightMeasurementInstructionBuilder();
    }

    public LogWeightMeasurementInstructionBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public LogWeightMeasurementInstructionBuilder withWeightInKg(BigDecimal weightInKg) {
        this.weightInKg = weightInKg;
        return this;
    }

    public LogWeightMeasurementInstruction build() {
        return new LogWeightMeasurementInstruction(date, weightInKg);
    }
}
