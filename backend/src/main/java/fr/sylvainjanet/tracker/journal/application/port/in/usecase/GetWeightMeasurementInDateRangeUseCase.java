package fr.sylvainjanet.tracker.journal.application.port.in.usecase;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementInDateRangeQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult;

public interface GetWeightMeasurementInDateRangeUseCase {

    GetWeightMeasurementInDateRangeResult get(GetWeightMeasurementInDateRangeQuery query);
}
