package fr.sylvainjanet.tracker.technical.domain.contract.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainEnum;
import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainValidated;
import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DomainValidatorTest {

    @Test
    void validatesAnObjectWithoutErrors() {
        TestValidated validated = new TestValidated(Set.of());

        assertThatCode(() -> DomainValidator.validate(validated)).doesNotThrowAnyException();
        assertThat(validated.validationCount()).isOne();
    }

    @Test
    void validatesAnObjectAndThrowsItsErrors() {
        TestValidated validated =
                new TestValidated(Set.of(error(TestErrorKind.FIRST_KIND, "first message", "one")));

        assertThatThrownBy(() -> DomainValidator.validate(validated))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessage(
                        "[DomainValidationError[kind=FIRST_KIND, messages=[PublicMessage: first"
                                + " message]]]");
        assertThat(validated.validationCount()).isOne();
    }

    @Test
    void acceptsAnEmptyErrorSet() {
        assertThatCode(() -> DomainValidator.throwErrors(Set.of())).doesNotThrowAnyException();
    }

    @Test
    void groupsErrorsByKindAndPublicMessage() {
        Set<DomainValidationError<TestErrorKind, TestErrorMetadata>> errors =
                Set.of(
                        error(TestErrorKind.FIRST_KIND, "alpha message", "one"),
                        error(TestErrorKind.FIRST_KIND, "beta message", "two"),
                        error(TestErrorKind.SECOND_KIND, "gamma message", "three"));

        DomainValidationException exception = catchValidationException(errors);

        assertThat(exception.getMessage())
                .startsWith("[")
                .endsWith("]")
                .containsOnlyOnce("DomainValidationError[kind=FIRST_KIND")
                .containsOnlyOnce("DomainValidationError[kind=SECOND_KIND")
                .containsOnlyOnce("PublicMessage: alpha message")
                .containsOnlyOnce("PublicMessage: beta message")
                .containsOnlyOnce("PublicMessage: gamma message")
                .doesNotContain("(x");
    }

    @Test
    void countsDistinctErrorsWithTheSameKindAndPublicMessage() {
        Set<DomainValidationError<TestErrorKind, TestErrorMetadata>> errors =
                Set.of(
                        error(TestErrorKind.FIRST_KIND, "shared message", "first detail"),
                        error(TestErrorKind.FIRST_KIND, "shared message", "second detail"));

        assertThat(catchValidationException(errors))
                .hasMessage(
                        "[DomainValidationError[kind=FIRST_KIND, messages=[PublicMessage (x2):"
                                + " shared message]]]");
    }

    @Test
    void countsTheSamePublicMessageSeparatelyForEachKind() {
        Set<DomainValidationError<TestErrorKind, TestErrorMetadata>> errors =
                Set.of(
                        error(TestErrorKind.FIRST_KIND, "shared message", "first detail"),
                        error(TestErrorKind.SECOND_KIND, "shared message", "second detail"));

        assertThat(catchValidationException(errors).getMessage())
                .containsOnlyOnce(
                        "DomainValidationError[kind=FIRST_KIND, messages=[PublicMessage: shared"
                                + " message]]")
                .containsOnlyOnce(
                        "DomainValidationError[kind=SECOND_KIND, messages=[PublicMessage: shared"
                                + " message]]")
                .doesNotContain("(x2)");
    }

    @Test
    void ordersKindsAndPublicMessagesAlphabetically() {
        Set<DomainValidationError<TestErrorKind, TestErrorMetadata>> errors = new LinkedHashSet<>();
        errors.add(error(TestErrorKind.SECOND_KIND, "delta message", "one"));
        errors.add(error(TestErrorKind.FIRST_KIND, "zeta message", "two"));
        errors.add(error(TestErrorKind.FIRST_KIND, "alpha message", "three"));
        errors.add(error(TestErrorKind.FIRST_KIND, "alpha message", "four"));

        assertThat(catchValidationException(errors))
                .hasMessage(
                        "[DomainValidationError[kind=FIRST_KIND, messages=[PublicMessage (x2):"
                                + " alpha message, PublicMessage: zeta message]],"
                                + " DomainValidationError[kind=SECOND_KIND, messages=[PublicMessage:"
                                + " delta message]]]");
    }

    @Test
    void rejectsNullInputs() {
        Set<DomainValidationError<TestErrorKind, TestErrorMetadata>> nullErrors = null;

        assertThatThrownBy(() -> DomainValidator.validate(null))
                .isExactlyInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> DomainValidator.throwErrors(nullErrors))
                .isExactlyInstanceOf(NullPointerException.class);
    }

    private static DomainValidationException catchValidationException(
            Set<DomainValidationError<TestErrorKind, TestErrorMetadata>> errors) {
        return org.assertj.core.api.Assertions.catchThrowableOfType(
                DomainValidationException.class, () -> DomainValidator.throwErrors(errors));
    }

    private static TestValidationError error(
            TestErrorKind kind, String publicMessage, String detail) {
        return new TestValidationError(kind, new TestErrorMetadata(publicMessage, detail));
    }

    private enum TestErrorKind implements DomainEnum<TestErrorKind> {
        SECOND_KIND,
        FIRST_KIND;

        @Override
        public String toString() {
            return super.toString();
        }
    }

    private static final class TestErrorMetadata extends DomainValidationErrorMetadata {

        private final String detail;

        private TestErrorMetadata(String publicMessage, String detail) {
            super(publicMessage);
            this.detail = detail;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof TestErrorMetadata that)) {
                return false;
            }
            return Objects.equals(publicMessage, that.publicMessage)
                    && Objects.equals(detail, that.detail);
        }

        @Override
        public int hashCode() {
            return Objects.hash(publicMessage, detail);
        }

        @Override
        public String toString() {
            return "TestErrorMetadata[publicMessage=" + publicMessage + ", detail=" + detail + "]";
        }
    }

    private static final class TestValidationError
            extends DomainValidationError<TestErrorKind, TestErrorMetadata> {

        private TestValidationError(TestErrorKind kind, TestErrorMetadata metadata) {
            super(kind, metadata);
        }
    }

    private static final class TestValidated
            implements DomainValidated<TestValidated, TestErrorKind, TestErrorMetadata> {

        private final Set<DomainValidationError<TestErrorKind, TestErrorMetadata>> errors;
        private int validationCount;

        private TestValidated(Set<DomainValidationError<TestErrorKind, TestErrorMetadata>> errors) {
            this.errors = errors;
        }

        @Override
        public Set<DomainValidationError<TestErrorKind, TestErrorMetadata>> validate() {
            validationCount++;
            return errors;
        }

        private int validationCount() {
            return validationCount;
        }
    }
}
