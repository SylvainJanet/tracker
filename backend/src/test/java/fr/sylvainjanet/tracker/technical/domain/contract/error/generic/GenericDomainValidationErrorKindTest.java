package fr.sylvainjanet.tracker.technical.domain.contract.error.generic;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GenericDomainValidationErrorKindTest {

    @Test
    void exposesTheGenericErrorKindAndItsStringRepresentation() {
        assertThat(GenericDomainValidationErrorKind.values())
                .containsExactly(GenericDomainValidationErrorKind.GENERIC_ERROR);
        assertThat(GenericDomainValidationErrorKind.valueOf("GENERIC_ERROR"))
                .isEqualTo(GenericDomainValidationErrorKind.GENERIC_ERROR);
        assertThat(GenericDomainValidationErrorKind.GENERIC_ERROR.toString())
                .isEqualTo("GENERIC_ERROR");
    }
}
