package fr.sylvainjanet.tracker.tracking.adapter.in.web.controller;

import fr.sylvainjanet.tracker.tracking.adapter.in.web.dtos.request.CreateDailyRecordRequest;
import fr.sylvainjanet.tracker.tracking.adapter.in.web.dtos.response.DailyRecordResponse;
import fr.sylvainjanet.tracker.tracking.application.port.in.exceptions.DailyRecordAlreadyExistsException;
import fr.sylvainjanet.tracker.tracking.application.port.in.usecase.CreateDailyRecordUseCase;
import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;
import fr.sylvainjanet.tracker.tracking.domain.Weight;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/daily-records")
public final class DailyRecordController {

    private final CreateDailyRecordUseCase createDailyRecordUseCase;

    public DailyRecordController(CreateDailyRecordUseCase createDailyRecordUseCase) {
        this.createDailyRecordUseCase = createDailyRecordUseCase;
    }

    @ApiResponse(
            responseCode = "201",
            description = "Daily record created",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DailyRecordResponse.class)))
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
    @ApiResponse(
            responseCode = "409",
            description = "Daily record already exists",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema =
                                    @Schema(
                                            implementation =
                                                    org.springframework.http.ProblemDetail.class)))
    @PostMapping
    ResponseEntity<DailyRecordResponse> create(@RequestBody @Valid CreateDailyRecordRequest request)
            throws DailyRecordAlreadyExistsException {
        DailyRecord result = createDailyRecordUseCase.execute(toCommand(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    private DailyRecordResponse toResponse(DailyRecord result) {
        return new DailyRecordResponse(result.date(), result.weightInKilograms());
    }

    private DailyRecord toCommand(CreateDailyRecordRequest request) {
        return DailyRecord.create(request.date(), Weight.of(request.weight()));
    }
}
