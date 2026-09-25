package fr.sylvainjanet.tracker.journal.application.port.out.gateway.store;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.GetWeightMeasurementByDateCriteria;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.GetWeightMeasurementInDateRangeCriteria;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.instruction.LogWeightMeasurementInstruction;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetFirstWeightMeasurementDateOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetWeightMeasurementByDateOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetWeightMeasurementInDateRangeOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.LogWeightMeasurementOutcome;
import java.util.Optional;

public interface WeightMeasurementStore {

    LogWeightMeasurementOutcome log(LogWeightMeasurementInstruction instruction);

    Optional<GetWeightMeasurementByDateOutcome> getByDate(
            GetWeightMeasurementByDateCriteria criteria);

    GetWeightMeasurementInDateRangeOutcome getInDateRange(
            GetWeightMeasurementInDateRangeCriteria criteria);

    Optional<GetFirstWeightMeasurementDateOutcome> getFirstWeightMeasurementDate();
}
