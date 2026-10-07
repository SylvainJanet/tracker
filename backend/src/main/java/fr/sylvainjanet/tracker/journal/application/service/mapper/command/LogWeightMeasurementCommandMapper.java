package fr.sylvainjanet.tracker.journal.application.service.mapper.command;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.command.LogWeightMeasurementCommand;
import fr.sylvainjanet.tracker.journal.domain.entity.WeightMeasurement;
import fr.sylvainjanet.tracker.journal.domain.value.Weight;

public final class LogWeightMeasurementCommandMapper {

    private LogWeightMeasurementCommandMapper() {}

    public static WeightMeasurement measurement(LogWeightMeasurementCommand command) {
        return WeightMeasurement.create(command.date(), Weight.create(command.weightInKg()));
    }
}
