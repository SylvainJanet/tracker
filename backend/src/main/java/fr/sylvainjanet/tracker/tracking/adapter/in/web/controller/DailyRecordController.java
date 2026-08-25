package fr.sylvainjanet.tracker.tracking.adapter.in.web.controller;

import fr.sylvainjanet.tracker.tracking.adapter.in.web.dtos.request.CreateDailyRecordRequest;
import fr.sylvainjanet.tracker.tracking.adapter.in.web.dtos.response.DailyRecordResponse;
import fr.sylvainjanet.tracker.tracking.adapter.in.web.dtos.response.enums.CompletionStatusResponse;
import fr.sylvainjanet.tracker.tracking.application.port.in.exceptions.DailyRecordAlreadyExistsException;
import fr.sylvainjanet.tracker.tracking.application.port.in.exceptions.DailyRecordNotFoundException;
import fr.sylvainjanet.tracker.tracking.application.port.in.usecase.CreateDailyRecordUseCase;
import fr.sylvainjanet.tracker.tracking.application.port.in.usecase.GetDailyRecordUseCase;
import fr.sylvainjanet.tracker.tracking.domain.CompletionStatus;
import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/daily-records")
public final class DailyRecordController {

    private final CreateDailyRecordUseCase createDailyRecordUseCase;
    private final GetDailyRecordUseCase getDailyRecordUseCase;

    public DailyRecordController(
            CreateDailyRecordUseCase createDailyRecordUseCase,
            GetDailyRecordUseCase getDailyRecordUseCase) {
        this.createDailyRecordUseCase = createDailyRecordUseCase;
        this.getDailyRecordUseCase = getDailyRecordUseCase;
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

        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{date}")
                        .buildAndExpand(result.date())
                        .toUri();

        return ResponseEntity.created(location).body(toResponse(result));
    }

    @ApiResponse(
            responseCode = "200",
            description = "Daily record found",
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
            responseCode = "404",
            description = "Daily record not found",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema =
                                    @Schema(
                                            implementation =
                                                    org.springframework.http.ProblemDetail.class)))
    @GetMapping("/{date}")
    ResponseEntity<DailyRecordResponse> getByDate(@PathVariable("date") LocalDate request)
            throws DailyRecordNotFoundException {
        DailyRecord result = getDailyRecordUseCase.execute(request);

        return ResponseEntity.ok(toResponse(result));
    }

    private DailyRecordResponse toResponse(DailyRecord result) {
        return new DailyRecordResponse(result.date(), toCompletionStatusResponse(result.status()));
    }

    private CompletionStatusResponse toCompletionStatusResponse(CompletionStatus status) {
        return switch (status) {
            case IN_PROGRESS -> CompletionStatusResponse.IN_PROGRESS;
            case COMPLETED -> CompletionStatusResponse.COMPLETED;
        };
    }

    private LocalDate toCommand(CreateDailyRecordRequest request) {
        return request.date();
    }
}
