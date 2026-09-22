package fr.sylvainjanet.tracker.journal.application.port.in.usecase;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementByDateQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementByDateResult;
import java.util.Optional;

public interface GetWeightMeasurementByDateUseCase {

    Optional<GetWeightMeasurementByDateResult> get(GetWeightMeasurementByDateQuery query);
}
