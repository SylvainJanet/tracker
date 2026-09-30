package fr.sylvainjanet.tracker.technical.domain.contract.error.generic;

import static fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage.publicErrorMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class GenericDomainValidationErrorMessageTest {

    @Test
    void hasValueSemantics() {
        GenericDomainValidationErrorMessage first = publicErrorMessage("public message");
        GenericDomainValidationErrorMessage equal = publicErrorMessage("public message");
        GenericDomainValidationErrorMessage different = publicErrorMessage("other message");

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(different)
                .isNotNull()
                .isNotEqualTo("public message");
    }

    @Test
    void hasAnExplicitStringRepresentation() {
        GenericDomainValidationErrorMessage message = publicErrorMessage("public message");

        assertThat(message)
                .hasToString("GenericDomainValidationErrorMessage{publicMessage='public message'}");
    }

    @Test
    void rejectsAMissingPublicMessage() {
        assertThatThrownBy(() -> publicErrorMessage(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("public message must not be null");
    }
}
