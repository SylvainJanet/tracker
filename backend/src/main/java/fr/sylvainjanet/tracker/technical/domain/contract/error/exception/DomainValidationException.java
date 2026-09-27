package fr.sylvainjanet.tracker.technical.domain.contract.error.exception;

public class DomainValidationException extends RuntimeException {
    public DomainValidationException(String message) {
        super(message);
    }
}
