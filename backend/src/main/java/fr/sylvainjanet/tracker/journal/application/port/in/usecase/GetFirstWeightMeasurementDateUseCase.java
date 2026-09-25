package fr.sylvainjanet.tracker.journal.application.port.in.usecase;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetFirstWeightMeasurementDateResult;
import java.util.Optional;

public interface GetFirstWeightMeasurementDateUseCase {
    Optional<GetFirstWeightMeasurementDateResult> get();
}
