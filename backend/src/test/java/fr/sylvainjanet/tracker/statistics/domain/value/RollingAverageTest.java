package fr.sylvainjanet.tracker.statistics.domain.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RollingAverageTest {

    private static final RollingWindow WINDOW = RollingWindow.create(7L);
    private static final RollingAveragePoint FIRST_POINT =
            RollingAveragePoint.create(2L, Set.of(1L, 2L), Value.create(2));
    private static final RollingAveragePoint SECOND_POINT =
            RollingAveragePoint.create(4L, Set.of(1L, 2L, 4L), Value.create(3));

    @Test
    void createsARollingAverage() {
        RollingAverage average = RollingAverage.create(WINDOW, List.of(FIRST_POINT, SECOND_POINT));

        assertThat(average.window()).isEqualTo(WINDOW);
        assertThat(average.windowSize()).isEqualTo(7L);
        assertThat(average.points()).containsExactly(FIRST_POINT, SECOND_POINT);
    }

    @Test
    void acceptsAnEmptyPointCollection() {
        assertThatCode(() -> RollingAverage.create(WINDOW, List.of())).doesNotThrowAnyException();
    }

    @Test
    void rejectsMissingWindowAndPointsTogether() {
        assertThatThrownBy(() -> RollingAverage.create(null, null))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Rolling window must not be null")
                .hasMessageContaining("Rolling average points must not be null");
    }

    @Test
    void rejectsANullPoint() {
        List<RollingAveragePoint> points = Collections.singletonList(null);

        assertThatThrownBy(() -> RollingAverage.create(WINDOW, points))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Rolling average point must not be null");
    }

    @Test
    void rejectsDescendingPointIndexes() {
        List<RollingAveragePoint> points = List.of(SECOND_POINT, FIRST_POINT);
        assertThatThrownBy(() -> RollingAverage.create(WINDOW, points))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining(
                        "Rolling average points must be ordered by unique ascending indexes");
    }

    @Test
    void rejectsDuplicatePointIndexes() {
        RollingAveragePoint duplicateIndex =
                RollingAveragePoint.create(2L, Set.of(2L), Value.create(4));

        List<RollingAveragePoint> points = List.of(FIRST_POINT, duplicateIndex);
        assertThatThrownBy(() -> RollingAverage.create(WINDOW, points))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining(
                        "Rolling average points must be ordered by unique ascending indexes");
    }

    @Test
    void protectsItsPointsFromMutation() {
        List<RollingAveragePoint> suppliedPoints =
                new ArrayList<>(List.of(FIRST_POINT, SECOND_POINT));
        RollingAverage average = RollingAverage.create(WINDOW, suppliedPoints);

        suppliedPoints.clear();

        List<RollingAveragePoint> points = average.points();
        assertThat(points).containsExactly(FIRST_POINT, SECOND_POINT);
        assertThatThrownBy(points::clear).isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void hasValueSemantics() {
        RollingAverage first = RollingAverage.create(WINDOW, List.of(FIRST_POINT, SECOND_POINT));
        RollingAverage equal = RollingAverage.create(WINDOW, List.of(FIRST_POINT, SECOND_POINT));
        RollingAverage differentWindow =
                RollingAverage.create(
                        RollingWindow.create(14L), List.of(FIRST_POINT, SECOND_POINT));
        RollingAverage differentPoints = RollingAverage.create(WINDOW, List.of(FIRST_POINT));

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(differentWindow)
                .isNotEqualTo(differentPoints)
                .isNotNull();
    }
}
