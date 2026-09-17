package fr.sylvainjanet.tracker.journal.application.port.in.usecase;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.command.LogWeightMeasurementCommand;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.LogWeightMeasurementResult;

public interface LogWeightMeasurementUseCase {

    LogWeightMeasurementResult log(LogWeightMeasurementCommand command);
}
