package fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.handler;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class CorrectlyNamedHandler {

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handle(RuntimeException exception) {
        return ProblemDetail.forStatus(500);
    }
}
