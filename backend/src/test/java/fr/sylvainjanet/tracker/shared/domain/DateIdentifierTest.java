package fr.sylvainjanet.tracker.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;

class DateIdentifierTest {

    private static final LocalDate DATE = LocalDate.of(2026, Month.SEPTEMBER, 29);

    @Test
    void rejectsANullDate() {
        assertThatThrownBy(() -> DateIdentifier.create(null))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Date cannot be null");
    }

    @Test
    void hasValueSemantics() {
        DateIdentifier first = DateIdentifier.create(DATE);
        DateIdentifier equal = DateIdentifier.create(DATE);
        DateIdentifier different = DateIdentifier.create(DATE.plusDays(1));

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(different)
                .isNotNull();
    }
}
