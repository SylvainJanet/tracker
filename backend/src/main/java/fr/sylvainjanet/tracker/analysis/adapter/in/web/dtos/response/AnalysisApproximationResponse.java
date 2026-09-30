package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response;

import java.math.BigDecimal;

public record AnalysisApproximationResponse(BigDecimal value, AnalysisRoundingResponse rounding) {

    public int displayOrder() {
        return rounding.displayOrder();
    }
}
