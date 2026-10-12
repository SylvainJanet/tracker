package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "PRETTY uses scale 2 and PRECISE uses scale 5; both use HALF_UP rounding")
public enum AnalysisRoundingResponse {
    PRECISE(1),
    PRETTY(0);

    private final int displayOrder;

    private AnalysisRoundingResponse(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public int displayOrder() {
        return displayOrder;
    }
}
