package fr.sylvainjanet.tracker.statistics.domain.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class IndexedValueTest {

    private static final Value VALUE = Value.create(new BigDecimal("80.00"));

    @Test
    void createsAnIndexedValue() {
        IndexedValue indexedValue = IndexedValue.create(4L, VALUE);

        assertThat(indexedValue.index()).isEqualTo(4L);
        assertThat(indexedValue.value()).isEqualTo(VALUE);
    }

    @Test
    void rejectsANullValue() {
        assertThatThrownBy(() -> IndexedValue.create(4L, null))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("indexed value must not be null");
    }

    @Test
    void hasValueSemantics() {
        IndexedValue first = IndexedValue.create(4L, VALUE);
        IndexedValue equal = IndexedValue.create(4L, VALUE);
        IndexedValue differentIndex = IndexedValue.create(5L, VALUE);
        IndexedValue differentValue =
                IndexedValue.create(4L, Value.create(new BigDecimal("82.00")));

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(differentIndex)
                .isNotEqualTo(differentValue)
                .isNotNull();
    }
}
