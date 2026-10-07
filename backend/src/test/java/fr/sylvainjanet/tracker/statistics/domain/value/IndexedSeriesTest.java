package fr.sylvainjanet.tracker.statistics.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.statistics.domain.value.IndexedSeries;
import fr.sylvainjanet.tracker.statistics.domain.value.IndexedValue;
import fr.sylvainjanet.tracker.statistics.domain.value.Value;
import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class IndexedSeriesTest {

    private static final IndexedValue FIRST_VALUE =
            IndexedValue.create(1L, Value.create(new BigDecimal("80.00")));
    private static final IndexedValue SECOND_VALUE =
            IndexedValue.create(4L, Value.create(new BigDecimal("82.00")));

    @Test
    void createsASparseIndexedSeries() {
        IndexedSeries series = IndexedSeries.create(List.of(FIRST_VALUE, SECOND_VALUE));

        assertThat(series).containsExactly(FIRST_VALUE, SECOND_VALUE);
    }

    @Test
    void acceptsAnEmptySeries() {
        assertThatCode(() -> IndexedSeries.create(List.of())).doesNotThrowAnyException();
    }

    @Test
    void rejectsDescendingIndexes() {
        List<IndexedValue> values = List.of(SECOND_VALUE, FIRST_VALUE);
        assertThatThrownBy(() -> IndexedSeries.create(values))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("indexed values must be ordered by unique ascending indexes");
    }

    @Test
    void rejectsDuplicateIndexes() {
        IndexedValue duplicateIndex =
                IndexedValue.create(1L, Value.create(new BigDecimal("82.00")));

        List<IndexedValue> values = List.of(FIRST_VALUE, duplicateIndex);
        assertThatThrownBy(() -> IndexedSeries.create(values))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("indexed values must be ordered by unique ascending indexes");
    }

    @Test
    void rejectsANullIndexedValue() {
        List<IndexedValue> values = Collections.singletonList(null);

        assertThatThrownBy(() -> IndexedSeries.create(values))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("indexed value must not be null");
    }

    @Test
    void protectsItsValuesFromMutation() {
        List<IndexedValue> suppliedValues = new ArrayList<>(List.of(FIRST_VALUE, SECOND_VALUE));
        IndexedSeries series = IndexedSeries.create(suppliedValues);

        suppliedValues.clear();

        assertThat(series).containsExactly(FIRST_VALUE, SECOND_VALUE);
        assertThatThrownBy(series::clear).isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void hasListValueSemantics() {
        IndexedSeries first = IndexedSeries.create(List.of(FIRST_VALUE, SECOND_VALUE));
        IndexedSeries equal = IndexedSeries.create(List.of(FIRST_VALUE, SECOND_VALUE));
        IndexedSeries different = IndexedSeries.create(List.of(FIRST_VALUE));

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(different)
                .isNotNull();
    }
}
