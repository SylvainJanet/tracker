package fr.sylvainjanet.tracker.analysis.adapter.in.web.controller;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.GetWeightAnalysisResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.mapper.GetWeightAnalysisResponseMapper;
import fr.sylvainjanet.tracker.analysis.application.port.in.usecase.GetWeightAnalysisUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Objects;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis")
public final class AnalysisController {

    private final GetWeightAnalysisUseCase getWeightAnalysis;

    public AnalysisController(GetWeightAnalysisUseCase getWeightAnalysis) {
        this.getWeightAnalysis =
                Objects.requireNonNull(getWeightAnalysis, "get weight analysis must not be null");
    }

    @Operation(
            summary = "Get weight analysis",
            description =
                    """
                    Returns measurements from the first logged weight through today, ordered by date.
                    Day numbers are one-based calendar-day offsets from the timeline start; missing
                    dates are omitted. When no measurement is available, the timeline and range are
                    null and both value collections are empty. Rolling averages are currently empty.
                    """)
    @ApiResponse(
            responseCode = "200",
            description = "Weight analysis retrieved",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GetWeightAnalysisResponse.class),
                            examples = {
                                @ExampleObject(
                                        name = "withMeasurements",
                                        summary = "Analysis containing measurements",
                                        value =
                                                """
                                            {
                                              "timelineStartDate": "2026-09-20",
                                              "range": {
                                                "startDate": "2026-09-20",
                                                "endDate": "2026-09-25"
                                              },
                                              "weightMeasurements": [
                                                {
                                                  "date": "2026-09-20",
                                                  "dayNumber": 1,
                                                  "weightInKg": 82.10
                                                },
                                                {
                                                  "date": "2026-09-23",
                                                  "dayNumber": 4,
                                                  "weightInKg": 81.90
                                                }
                                              ],
                                              "rollingAverages": []
                                            }
                                            """),
                                @ExampleObject(
                                        name = "withoutMeasurements",
                                        summary = "Globally empty analysis",
                                        value =
                                                """
                                            {
                                              "timelineStartDate": null,
                                              "range": null,
                                              "weightMeasurements": [],
                                              "rollingAverages": []
                                            }
                                            """)
                            }))
    @GetMapping(path = "/weight")
    ResponseEntity<GetWeightAnalysisResponse> getWeightAnalysis() {
        return ResponseEntity.ok(
                GetWeightAnalysisResponseMapper.resultToResponse(getWeightAnalysis.get()));
    }
}
