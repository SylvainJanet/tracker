package fr.sylvainjanet.tracker.journal.application.port.out.dtos.builders.instruction;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.instruction.LogWeightMeasurementInstruction;
import java.time.LocalDate;

public class LogWeightMeasurementInstructionTestBuilder {
    private LocalDate date;
    private Float weightInKg;

    public static LogWeightMeasurementInstructionTestBuilder aLogWeightMeasurementInstruction() {
        return new LogWeightMeasurementInstructionTestBuilder();
    }

    public LogWeightMeasurementInstructionTestBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public LogWeightMeasurementInstructionTestBuilder withWeightInKg(Float weightInKg) {
        this.weightInKg = weightInKg;
        return this;
    }

    public LogWeightMeasurementInstruction build() {
        return new LogWeightMeasurementInstruction(date, weightInKg);
    }
}
