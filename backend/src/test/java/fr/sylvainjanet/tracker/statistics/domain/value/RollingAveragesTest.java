package fr.sylvainjanet.tracker.statistics.domain.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class RollingAveragesTest {

    private static final RollingAverage SEVEN_DAY_AVERAGE =
            RollingAverage.create(RollingWindow.create(7L), List.of());
    private static final RollingAverage FOURTEEN_DAY_AVERAGE =
            RollingAverage.create(RollingWindow.create(14L), List.of());

    @Test
    void createsAscendingRollingAverages() {
        RollingAverages averages =
                RollingAverages.create(List.of(SEVEN_DAY_AVERAGE, FOURTEEN_DAY_AVERAGE));

        assertThat(averages).containsExactly(SEVEN_DAY_AVERAGE, FOURTEEN_DAY_AVERAGE);
    }

    @Test
    void acceptsAnEmptyAverageCollection() {
        assertThatCode(() -> RollingAverages.create(List.of())).doesNotThrowAnyException();
    }

    @Test
    void rejectsDescendingWindowSizes() {
        List<RollingAverage> averages = List.of(FOURTEEN_DAY_AVERAGE, SEVEN_DAY_AVERAGE);
        assertThatThrownBy(() -> RollingAverages.create(averages))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining(
                        "rolling averages must be ordered by unique ascending window sizes");
    }

    @Test
    void rejectsDuplicateWindowSizes() {
        RollingAverage duplicateWindowSize =
                RollingAverage.create(RollingWindow.create(7L), List.of());

        List<RollingAverage> averages = List.of(SEVEN_DAY_AVERAGE, duplicateWindowSize);
        assertThatThrownBy(() -> RollingAverages.create(averages))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining(
                        "rolling averages must be ordered by unique ascending window sizes");
    }

    @Test
    void rejectsANullRollingAverage() {
        List<RollingAverage> averages = Collections.singletonList(null);

        assertThatThrownBy(() -> RollingAverages.create(averages))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("rolling average must not be null");
    }

    @Test
    void protectsItsAveragesFromMutation() {
        List<RollingAverage> suppliedAverages =
                new ArrayList<>(List.of(SEVEN_DAY_AVERAGE, FOURTEEN_DAY_AVERAGE));
        RollingAverages averages = RollingAverages.create(suppliedAverages);

        suppliedAverages.clear();

        assertThat(averages).containsExactly(SEVEN_DAY_AVERAGE, FOURTEEN_DAY_AVERAGE);
        assertThatThrownBy(averages::clear)
                .isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void hasListValueSemantics() {
        RollingAverages first =
                RollingAverages.create(List.of(SEVEN_DAY_AVERAGE, FOURTEEN_DAY_AVERAGE));
        RollingAverages equal =
                RollingAverages.create(List.of(SEVEN_DAY_AVERAGE, FOURTEEN_DAY_AVERAGE));
        RollingAverages different = RollingAverages.create(List.of(SEVEN_DAY_AVERAGE));

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(different)
                .isNotNull();
    }
}
