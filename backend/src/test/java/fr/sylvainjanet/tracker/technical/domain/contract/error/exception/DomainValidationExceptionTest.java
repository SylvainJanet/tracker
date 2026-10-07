package fr.sylvainjanet.tracker.technical.domain.contract.error.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DomainValidationExceptionTest {

    @Test
    void isARuntimeExceptionThatPreservesItsMessage() {
        DomainValidationException exception = new DomainValidationException("validation failed");

        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("validation failed")
                .hasNoCause();
    }
}
