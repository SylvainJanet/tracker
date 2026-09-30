package fr.sylvainjanet.tracker.statistics.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.statistics.domain.value.IndexRange;
import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

class IndexRangeTest {

    @Test
    void representsInclusiveIndexBounds() {
        IndexRange range = IndexRange.create(3L, 5L);

        assertThat(range.start()).isEqualTo(3L);
        assertThat(range.end()).isEqualTo(5L);
        assertThat(range.contains(3L)).isTrue();
        assertThat(range.contains(4L)).isTrue();
        assertThat(range.contains(5L)).isTrue();
        assertThat(range.contains(2L)).isFalse();
        assertThat(range.contains(6L)).isFalse();
    }

    @Test
    void acceptsTheSameIndexAsBothBounds() {
        assertThatCode(() -> IndexRange.create(3L, 3L)).doesNotThrowAnyException();
    }

    @Test
    void rejectsReversedBounds() {
        assertThatThrownBy(() -> IndexRange.create(5L, 4L))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("first index must not be after last index");
    }

    @Test
    void hasValueSemantics() {
        IndexRange first = IndexRange.create(3L, 5L);
        IndexRange equal = IndexRange.create(3L, 5L);
        IndexRange differentStart = IndexRange.create(2L, 5L);
        IndexRange differentEnd = IndexRange.create(3L, 6L);

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(differentStart)
                .isNotEqualTo(differentEnd)
                .isNotNull();
    }
}
