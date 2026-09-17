package fr.sylvainjanet.tracker.tracking.adapter.in.web.handler;

import fr.sylvainjanet.tracker.tracking.application.port.in.exceptions.DailyRecordAlreadyExistsException;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public final class DailyRecordExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DailyRecordAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ProblemDetail handleDailyRecordAlreadyExists(DailyRecordAlreadyExistsException exception) {

        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());

        problem.setTitle("Daily record already exists");
        problem.setProperty("date", exception.date().toString());

        return problem;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ProblemDetail handleUnexpected(Exception exception) {

        String errorId = UUID.randomUUID().toString();

        logger.error("Unhandled HTTP failure; errorId=" + errorId, exception);

        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");

        problem.setTitle("Internal server error");
        problem.setProperty("errorId", errorId);

        return problem;
    }
}
