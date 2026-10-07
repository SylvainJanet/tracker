package fr.sylvainjanet.tracker.technical.domain.contract.error.generic;

import static fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationError.genericError;
import static fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage.publicErrorMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class GenericDomainValidationErrorTest {

    @Test
    void createsAGenericErrorWithPublicMetadata() {
        GenericDomainValidationError error = genericError("public message");

        assertThat(error.kind()).isEqualTo(GenericDomainValidationErrorKind.GENERIC_ERROR);
        assertThat(error.metadata()).isEqualTo(publicErrorMessage("public message"));
        assertThat(error.publicMessage()).isEqualTo("public message");
    }

    @Test
    void hasValueSemantics() {
        GenericDomainValidationError first = genericError("public message");
        GenericDomainValidationError equal = genericError("public message");
        GenericDomainValidationError different = genericError("other message");

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(different)
                .isNotNull()
                .isNotEqualTo("public message");
    }

    @Test
    void hasAnExplicitStringRepresentation() {
        GenericDomainValidationError error = genericError("public message");

        assertThat(error)
                .hasToString(
                        "GenericDomainValidationError[kind=GENERIC_ERROR,"
                                + " metadata=GenericDomainValidationErrorMessage{publicMessage='public"
                                + " message'}]");
    }

    @Test
    void rejectsAMissingPublicMessage() {
        assertThatThrownBy(() -> genericError(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("public message must not be null");
    }
}
