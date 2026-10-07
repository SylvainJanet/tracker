package fr.sylvainjanet.tracker.statistics.domain.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RollingWindowTest {

    @Test
    void representsAnInclusiveRollingWindow() {
        RollingWindow window = RollingWindow.create(7L);

        assertThat(window.size()).isEqualTo(7L);
        assertThat(window.startFromEnd(10L)).isEqualTo(4L);
    }

    @Test
    void aSingleValueWindowStartsAtItsEnd() {
        RollingWindow window = RollingWindow.create(1L);

        assertThat(window.startFromEnd(10L)).isEqualTo(10L);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L})
    void requiresAPositiveSize(long size) {
        assertThatThrownBy(() -> RollingWindow.create(size))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("rolling window size must be positive");
    }

    @Test
    void hasValueSemantics() {
        RollingWindow first = RollingWindow.create(7L);
        RollingWindow equal = RollingWindow.create(7L);
        RollingWindow different = RollingWindow.create(14L);

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(different)
                .isNotNull();
    }
}
