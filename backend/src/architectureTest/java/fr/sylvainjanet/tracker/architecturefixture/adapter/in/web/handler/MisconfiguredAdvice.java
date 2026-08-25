package fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.handler;

import org.springframework.web.bind.annotation.ExceptionHandler;

public class MisconfiguredAdvice {

    @ExceptionHandler(RuntimeException.class)
    public String handle(RuntimeException exception) {
        return exception.getMessage();
    }
}
