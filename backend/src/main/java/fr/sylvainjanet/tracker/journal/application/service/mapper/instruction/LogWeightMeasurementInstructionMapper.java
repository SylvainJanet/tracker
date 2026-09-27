package fr.sylvainjanet.tracker.journal.application.service.mapper.instruction;

import static fr.sylvainjanet.tracker.journal.application.port.out.dtos.instruction.builder.LogWeightMeasurementInstructionBuilder.aLogWeightMeasurementInstruction;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.instruction.LogWeightMeasurementInstruction;
import fr.sylvainjanet.tracker.journal.domain.entity.WeightMeasurement;

public final class LogWeightMeasurementInstructionMapper {

    private LogWeightMeasurementInstructionMapper() {}

    public static LogWeightMeasurementInstruction logInstruction(WeightMeasurement measurement) {
        return aLogWeightMeasurementInstruction()
                .withDate(measurement.date())
                .withWeightInKg(measurement.weightInKilograms())
                .build();
    }
}
