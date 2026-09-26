package fr.sylvainjanet.tracker.journal.application.port.in.usecase;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementInDateRangeQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult;

public interface GetWeightMeasurementInDateRangeUseCase {

    /** Returns existing measurements within the inclusive bounds, ordered by ascending date. */
    GetWeightMeasurementInDateRangeResult get(GetWeightMeasurementInDateRangeQuery query);
}
