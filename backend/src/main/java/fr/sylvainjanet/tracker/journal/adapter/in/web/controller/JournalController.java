package fr.sylvainjanet.tracker.journal.adapter.in.web.controller;

import fr.sylvainjanet.tracker.journal.adapter.in.web.dtos.request.LogWeightMeasurementRequest;
import fr.sylvainjanet.tracker.journal.adapter.in.web.dtos.response.GetWeightMeasurementByDateResponse;
import fr.sylvainjanet.tracker.journal.adapter.in.web.dtos.response.LogWeightMeasurementResponse;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.command.LogWeightMeasurementCommand;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementByDateQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementByDateResult;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.LogWeightMeasurementResult;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetWeightMeasurementByDateUseCase;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.LogWeightMeasurementUseCase;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/journal")
public final class JournalController {

    private final LogWeightMeasurementUseCase logWeightMeasurementUseCase;
    private final GetWeightMeasurementByDateUseCase getWeightMeasurementByDateUseCase;

    public JournalController(
            LogWeightMeasurementUseCase logWeightMeasurementUseCase,
            GetWeightMeasurementByDateUseCase getWeightMeasurementByDateUseCase) {
        this.getWeightMeasurementByDateUseCase = getWeightMeasurementByDateUseCase;
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

    @ApiResponse(
            responseCode = "200",
            description = "Weight measurement found",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema =
                                    @Schema(
                                            implementation =
                                                    GetWeightMeasurementByDateResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Weight measurement not found",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema =
                                    @Schema(
                                            implementation =
                                                    org.springframework.http.ProblemDetail.class)))
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
    @GetMapping(path = "/weight-measurement/{date}")
    ResponseEntity<GetWeightMeasurementByDateResponse> get(@PathVariable("date") LocalDate date) {
        Optional<GetWeightMeasurementByDateResult> result =
                getWeightMeasurementByDateUseCase.get(toQuery(date));
        return result.map(
                        getWeightMeasurementByDateResult ->
                                ResponseEntity.ok(toResponse(getWeightMeasurementByDateResult)))
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No weight measurement was found for date " + date));
    }

    private LogWeightMeasurementCommand toCommand(LogWeightMeasurementRequest request) {
        return new LogWeightMeasurementCommand(request.date(), request.weightInKg());
    }

    private GetWeightMeasurementByDateQuery toQuery(LocalDate date) {
        return new GetWeightMeasurementByDateQuery(date);
    }

    private LogWeightMeasurementResponse toResponse(LogWeightMeasurementResult result) {
        return new LogWeightMeasurementResponse(result.date(), result.weightInKg());
    }

    private GetWeightMeasurementByDateResponse toResponse(GetWeightMeasurementByDateResult result) {
        return new GetWeightMeasurementByDateResponse(result.date(), result.weightInKg());
    }
}
