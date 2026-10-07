package fr.sylvainjanet.tracker.statistics.domain.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RollingAveragePointTest {

    private static final Value AVERAGE = Value.create(5);

    @Test
    void createsARollingAveragePoint() {
        RollingAveragePoint point = RollingAveragePoint.create(4L, Set.of(1L, 2L, 4L), AVERAGE);

        assertThat(point.index()).isEqualTo(4L);
        assertThat(point.includedIndexes()).containsExactlyInAnyOrder(1L, 2L, 4L);
        assertThat(point.rollingAverage()).isEqualTo(AVERAGE);
    }

    @Test
    void rejectsMissingIncludedIndexesAndAverageTogether() {
        assertThatThrownBy(() -> RollingAveragePoint.create(4L, null, null))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Included indexes must not be null")
                .hasMessageContaining("Rolling average must not be null");
    }

    @Test
    void protectsItsIncludedIndexesFromMutation() {
        Set<Long> suppliedIndexes = new HashSet<>(Set.of(1L, 2L, 4L));
        RollingAveragePoint point = RollingAveragePoint.create(4L, suppliedIndexes, AVERAGE);

        suppliedIndexes.clear();

        Set<Long> includedIndexes = point.includedIndexes();
        assertThat(includedIndexes).containsExactlyInAnyOrder(1L, 2L, 4L);
        assertThatThrownBy(includedIndexes::clear)
                .isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void hasValueSemantics() {
        RollingAveragePoint first = RollingAveragePoint.create(4L, Set.of(1L, 2L, 4L), AVERAGE);
        RollingAveragePoint equal = RollingAveragePoint.create(4L, Set.of(4L, 2L, 1L), AVERAGE);
        RollingAveragePoint differentIndex =
                RollingAveragePoint.create(5L, Set.of(1L, 2L, 4L), AVERAGE);
        RollingAveragePoint differentIncludedIndexes =
                RollingAveragePoint.create(4L, Set.of(2L, 4L), AVERAGE);
        RollingAveragePoint differentAverage =
                RollingAveragePoint.create(4L, Set.of(1L, 2L, 4L), Value.create(6));

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(differentIndex)
                .isNotEqualTo(differentIncludedIndexes)
                .isNotEqualTo(differentAverage)
                .isNotNull();
    }
}
