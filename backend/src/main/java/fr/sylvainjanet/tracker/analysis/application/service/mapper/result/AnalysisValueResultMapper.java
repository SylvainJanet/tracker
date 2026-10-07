package fr.sylvainjanet.tracker.analysis.application.service.mapper.result;

import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.AnalysisValueResultBuilder.anAnalysisValueResult;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.AnalysisValueResult;
import fr.sylvainjanet.tracker.analysis.domain.value.Data;
import fr.sylvainjanet.tracker.analysis.domain.value.DatedValue;

public final class AnalysisValueResultMapper {

    private AnalysisValueResultMapper() {}

    public static AnalysisValueResult analysisValue(Data data, DatedValue datedValue) {
        return anAnalysisValueResult()
                .withDate(datedValue.date())
                .withDayNumber(data.indexFor(datedValue.date()))
                .withWeightInKg(datedValue.value())
                .build();
    }
}
