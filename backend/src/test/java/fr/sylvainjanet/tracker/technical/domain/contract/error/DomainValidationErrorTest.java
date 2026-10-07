package fr.sylvainjanet.tracker.technical.domain.contract.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainEnum;
import org.junit.jupiter.api.Test;

class DomainValidationErrorTest {

    @Test
    void exposesItsKindMetadataAndPublicMessage() {
        TestMetadata metadata = new TestMetadata("public message");

        TestValidationError error = new TestValidationError(TestKind.FIRST_KIND, metadata);

        assertThat(error.kind()).isEqualTo(TestKind.FIRST_KIND);
        assertThat(error.metadata()).isSameAs(metadata);
        assertThat(error.publicMessage()).isEqualTo("public message");
    }

    @Test
    void hasValueSemanticsWithinTheSameConcreteErrorType() {
        TestValidationError first =
                new TestValidationError(TestKind.FIRST_KIND, new TestMetadata("message"));
        TestValidationError equal =
                new TestValidationError(TestKind.FIRST_KIND, new TestMetadata("message"));
        TestValidationError differentKind =
                new TestValidationError(TestKind.SECOND_KIND, new TestMetadata("message"));
        TestValidationError differentMetadata =
                new TestValidationError(TestKind.FIRST_KIND, new TestMetadata("other message"));
        OtherValidationError otherType =
                new OtherValidationError(TestKind.FIRST_KIND, new TestMetadata("message"));

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(differentKind)
                .isNotEqualTo(differentMetadata)
                .isNotEqualTo(otherType)
                .isNotNull()
                .isNotEqualTo("message");
    }

    @Test
    void hasAnExplicitStringRepresentation() {
        TestValidationError error =
                new TestValidationError(TestKind.FIRST_KIND, new TestMetadata("public message"));

        assertThat(error)
                .hasToString(
                        "DomainValidationError[kind=FIRST_KIND,"
                                + " metadata=DomainValidationErrorMetadata{publicMessage='public"
                                + " message'}]");
    }

    @Test
    void rejectsMissingKindAndMetadata() {
        TestMetadata metadata = new TestMetadata("public message");

        assertThatThrownBy(() -> new TestValidationError(null, metadata))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("kind must not be null");
        assertThatThrownBy(() -> new TestValidationError(TestKind.FIRST_KIND, null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("metadata must not be null");
    }

    private enum TestKind implements DomainEnum<TestKind> {
        FIRST_KIND,
        SECOND_KIND;

        @Override
        public String toString() {
            return super.toString();
        }
    }

    private static final class TestMetadata extends DomainValidationErrorMetadata {

        private TestMetadata(String publicMessage) {
            super(publicMessage);
        }
    }

    private static final class TestValidationError
            extends DomainValidationError<TestKind, TestMetadata> {

        private TestValidationError(TestKind kind, TestMetadata metadata) {
            super(kind, metadata);
        }
    }

    private static final class OtherValidationError
            extends DomainValidationError<TestKind, TestMetadata> {

        private OtherValidationError(TestKind kind, TestMetadata metadata) {
            super(kind, metadata);
        }
    }
}
