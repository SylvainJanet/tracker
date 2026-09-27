package fr.sylvainjanet.tracker.importer.source;

import java.util.List;

public record WeightMeasurementSource(
        List<WeightMeasurementSourceRow> measurements,
        int sourceRowCount,
        int skippedBlankWeightCount,
        int skippedAfterCutoffCount) {}
