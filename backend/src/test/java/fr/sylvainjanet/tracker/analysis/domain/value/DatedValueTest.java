package fr.sylvainjanet.tracker.analysis.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.analysis.domain.value.DatedValue;
import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;

class DatedValueTest {

    private static final LocalDate DATE = LocalDate.of(2026, Month.SEPTEMBER, 23);
    private static final BigDecimal VALUE = new BigDecimal("12.34");

    @Test
    void createsADatedValue() {
        DatedValue datedValue = DatedValue.create(DATE, VALUE);

        assertThat(datedValue.date()).isEqualTo(DATE);
        assertThat(datedValue.value()).isEqualTo(VALUE);
    }

    @Test
    void rejectsMissingDateAndValueTogether() {
        assertThatThrownBy(() -> DatedValue.create(null, null))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("date must not be null")
                .hasMessageContaining("value must not be null");
    }

    @Test
    void hasValueSemantics() {
        DatedValue first = DatedValue.create(DATE, VALUE);
        DatedValue equal = DatedValue.create(DATE, VALUE);
        DatedValue differentDate = DatedValue.create(DATE.plusDays(1), VALUE);
        DatedValue differentValue = DatedValue.create(DATE, new BigDecimal("56.78"));

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(differentDate)
                .isNotEqualTo(differentValue)
                .isNotNull();
    }
}
