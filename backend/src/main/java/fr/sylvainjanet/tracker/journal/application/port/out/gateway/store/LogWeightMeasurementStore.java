package fr.sylvainjanet.tracker.journal.application.port.out.gateway.store;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.instruction.LogWeightMeasurementInstruction;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.LogWeightMeasurementOutcome;

public interface LogWeightMeasurementStore {

    LogWeightMeasurementOutcome log(LogWeightMeasurementInstruction instruction);
}
