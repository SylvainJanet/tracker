package fr.sylvainjanet.tracker.analysis.adapter.in.web.controller;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.GetWeightAnalysisResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.mapper.GetWeightAnalysisResponseMapper;
import fr.sylvainjanet.tracker.analysis.application.port.in.usecase.GetWeightAnalysisUseCase;
import io.swagger.v3.oas.annotations.media.Content;
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

    @ApiResponse(
            responseCode = "200",
            description = "Weight analysis retrieved",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GetWeightAnalysisResponse.class)))
    @GetMapping(path = "/weight")
    ResponseEntity<GetWeightAnalysisResponse> getWeightAnalysis() {
        return ResponseEntity.ok(
                GetWeightAnalysisResponseMapper.resultToResponse(getWeightAnalysis.get()));
    }
}
