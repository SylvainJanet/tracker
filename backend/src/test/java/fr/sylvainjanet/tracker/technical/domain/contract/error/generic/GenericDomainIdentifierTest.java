package fr.sylvainjanet.tracker.technical.domain.contract.error.generic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GenericDomainIdentifierTest {

    @ParameterizedTest
    @ValueSource(longs = {1L, Long.MAX_VALUE})
    void acceptsPositiveIdentifiers(long id) {
        assertThatCode(() -> new GenericDomainIdentifier(id)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, -1L, 0L})
    void rejectsNonPositiveIdentifiers(long id) {
        assertThatThrownBy(() -> new GenericDomainIdentifier(id))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessage(
                        "[DomainValidationError[kind=GENERIC_ERROR,"
                                + " messages=[PublicMessage: id must be positive]]]");
    }

    @Test
    void exposesNoValidationErrorsForAConstructedIdentifier() {
        GenericDomainIdentifier identifier = new GenericDomainIdentifier(42L);

        assertThat(identifier.validate()).isEmpty();
    }

    @Test
    void hasValueSemantics() {
        GenericDomainIdentifier first = new GenericDomainIdentifier(42L);
        GenericDomainIdentifier equal = new GenericDomainIdentifier(42L);
        GenericDomainIdentifier different = new GenericDomainIdentifier(43L);

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(different)
                .isNotNull()
                .isNotEqualTo(42L);
    }

    @Test
    void hasAnExplicitStringRepresentation() {
        GenericDomainIdentifier identifier = new GenericDomainIdentifier(42L);

        assertThat(identifier).hasToString("GenericDomainIdentifier{id=42}");
    }
}
