package fr.sylvainjanet.tracker.journal.application.port.out.gateway.store;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.GetWeightMeasurementByDateCriteria;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.GetWeightMeasurementInDateRangeCriteria;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.instruction.LogWeightMeasurementInstruction;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetFirstWeightMeasurementDateOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetWeightMeasurementInDateRangeOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.WeightMeasurementOutcome;
import java.util.Optional;

public interface WeightMeasurementStore {

    WeightMeasurementOutcome log(LogWeightMeasurementInstruction instruction);

    Optional<WeightMeasurementOutcome> getByDate(GetWeightMeasurementByDateCriteria criteria);

    GetWeightMeasurementInDateRangeOutcome getInDateRange(
            GetWeightMeasurementInDateRangeCriteria criteria);

    Optional<GetFirstWeightMeasurementDateOutcome> getFirstWeightMeasurementDate();
}
