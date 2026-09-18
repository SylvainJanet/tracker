package fr.sylvainjanet.tracker.journal.adapter.in.web.controller;

import fr.sylvainjanet.tracker.journal.adapter.in.web.dtos.request.LogWeightMeasurementRequest;
import fr.sylvainjanet.tracker.journal.adapter.in.web.dtos.response.LogWeightMeasurementResponse;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.command.LogWeightMeasurementCommand;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.LogWeightMeasurementResult;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.LogWeightMeasurementUseCase;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/journal")
public final class JournalController {

    private final LogWeightMeasurementUseCase logWeightMeasurementUseCase;

    public JournalController(LogWeightMeasurementUseCase logWeightMeasurementUseCase) {
        this.logWeightMeasurementUseCase = logWeightMeasurementUseCase;
    }

    @ApiResponse(
            responseCode = "200",
            description = "Weight measurement created or updated",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LogWeightMeasurementResponse.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema =
                                    @Schema(
                                            implementation =
                                                    org.springframework.http.ProblemDetail.class)))
    @PostMapping(path = "/weight-measurement")
    ResponseEntity<LogWeightMeasurementResponse> log(
            @RequestBody @Valid LogWeightMeasurementRequest request) {
        LogWeightMeasurementResult result = logWeightMeasurementUseCase.log(toCommand(request));
        return ResponseEntity.ok(toResponse(result));
    }

    private LogWeightMeasurementCommand toCommand(LogWeightMeasurementRequest request) {
        return new LogWeightMeasurementCommand(request.date(), request.weightInKg());
    }

    private LogWeightMeasurementResponse toResponse(LogWeightMeasurementResult result) {
        return new LogWeightMeasurementResponse(result.date(), result.weightInKg());
    }
}
