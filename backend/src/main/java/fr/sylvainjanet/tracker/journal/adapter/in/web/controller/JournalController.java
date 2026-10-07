package fr.sylvainjanet.tracker.journal.adapter.in.web.controller;

import fr.sylvainjanet.tracker.journal.adapter.in.web.controller.mapper.GetWeightMeasurementByDateQueryMapper;
import fr.sylvainjanet.tracker.journal.adapter.in.web.controller.mapper.LogWeightMeasurementCommandMapper;
import fr.sylvainjanet.tracker.journal.adapter.in.web.controller.mapper.WeightMeasurementResponseMapper;
import fr.sylvainjanet.tracker.journal.adapter.in.web.dtos.request.LogWeightMeasurementRequest;
import fr.sylvainjanet.tracker.journal.adapter.in.web.dtos.response.WeightMeasurementResponse;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetWeightMeasurementByDateUseCase;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.LogWeightMeasurementUseCase;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
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
                            schema = @Schema(implementation = WeightMeasurementResponse.class)))
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
    ResponseEntity<WeightMeasurementResponse> log(
            @RequestBody @Valid LogWeightMeasurementRequest request) {
        return ResponseEntity.ok(
                WeightMeasurementResponseMapper.response(
                        logWeightMeasurementUseCase.log(
                                LogWeightMeasurementCommandMapper.command(request))));
    }

    @ApiResponse(
            responseCode = "200",
            description = "Weight measurement found",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = WeightMeasurementResponse.class)))
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
    ResponseEntity<WeightMeasurementResponse> get(@PathVariable("date") LocalDate date) {
        return getWeightMeasurementByDateUseCase
                .get(GetWeightMeasurementByDateQueryMapper.query(date))
                .map(WeightMeasurementResponseMapper::response)
                .map(ResponseEntity::ok)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No weight measurement was found for date " + date));
    }
}
