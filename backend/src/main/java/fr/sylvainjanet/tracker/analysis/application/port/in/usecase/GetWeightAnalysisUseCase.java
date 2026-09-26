package fr.sylvainjanet.tracker.analysis.application.port.in.usecase;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;

public interface GetWeightAnalysisUseCase {

    /**
     * Returns analysis from the first logged weight through today, using a one-based calendar-day
     * timeline. Returns {@link GetWeightAnalysisResult#noMeasurements()} when no measurement is
     * available.
     */
    GetWeightAnalysisResult get();
}
