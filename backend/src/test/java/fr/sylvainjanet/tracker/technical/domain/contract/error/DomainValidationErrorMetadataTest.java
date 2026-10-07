package fr.sylvainjanet.tracker.technical.domain.contract.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class DomainValidationErrorMetadataTest {

    @Test
    void exposesItsPublicMessageToErrorImplementations() {
        TestMetadata metadata = new TestMetadata("public message");

        assertThat(metadata.exposedPublicMessage()).isEqualTo("public message");
    }

    @Test
    void hasValueSemanticsWithinTheSameConcreteMetadataType() {
        TestMetadata first = new TestMetadata("public message");
        TestMetadata equal = new TestMetadata("public message");
        TestMetadata different = new TestMetadata("other message");
        OtherMetadata otherType = new OtherMetadata("public message");

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(different)
                .isNotEqualTo(otherType)
                .isNotNull()
                .isNotEqualTo("public message");
    }

    @Test
    void hasAnExplicitStringRepresentation() {
        TestMetadata metadata = new TestMetadata("public message");

        assertThat(metadata)
                .hasToString("DomainValidationErrorMetadata{publicMessage='public message'}");
    }

    @Test
    void rejectsAMissingPublicMessage() {
        assertThatThrownBy(() -> new TestMetadata(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("public message must not be null");
    }

    private static final class TestMetadata extends DomainValidationErrorMetadata {

        private TestMetadata(String publicMessage) {
            super(publicMessage);
        }

        private String exposedPublicMessage() {
            return publicMessage();
        }
    }

    private static final class OtherMetadata extends DomainValidationErrorMetadata {

        private OtherMetadata(String publicMessage) {
            super(publicMessage);
        }
    }
}
