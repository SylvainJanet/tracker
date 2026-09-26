package fr.sylvainjanet.tracker.journal.application.port.in.usecase;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.command.LogWeightMeasurementCommand;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.LogWeightMeasurementResult;

public interface LogWeightMeasurementUseCase {

    /** Creates or replaces the weight measurement for the command's date. */
    LogWeightMeasurementResult log(LogWeightMeasurementCommand command);
}
