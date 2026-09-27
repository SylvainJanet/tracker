package fr.sylvainjanet.tracker.statistics.domain.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class RollingWindowsTest {

    private static final RollingWindow SEVEN_DAY_WINDOW = RollingWindow.create(7L);
    private static final RollingWindow FOURTEEN_DAY_WINDOW = RollingWindow.create(14L);

    @Test
    void createsAscendingRollingWindows() {
        RollingWindows windows =
                RollingWindows.create(List.of(SEVEN_DAY_WINDOW, FOURTEEN_DAY_WINDOW));

        assertThat(windows).containsExactly(SEVEN_DAY_WINDOW, FOURTEEN_DAY_WINDOW);
    }

    @Test
    void acceptsAnEmptyWindowCollection() {
        assertThatCode(() -> RollingWindows.create(List.of())).doesNotThrowAnyException();
    }

    @Test
    void rejectsDescendingWindowSizes() {
        List<RollingWindow> windows = List.of(FOURTEEN_DAY_WINDOW, SEVEN_DAY_WINDOW);
        assertThatThrownBy(() -> RollingWindows.create(windows))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("rolling windows must be ordered by unique ascending sizes");
    }

    @Test
    void rejectsDuplicateWindowSizes() {
        List<RollingWindow> windows = List.of(SEVEN_DAY_WINDOW, RollingWindow.create(7L));
        assertThatThrownBy(() -> RollingWindows.create(windows))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("rolling windows must be ordered by unique ascending sizes");
    }

    @Test
    void rejectsANullRollingWindow() {
        List<RollingWindow> windows = Collections.singletonList(null);

        assertThatThrownBy(() -> RollingWindows.create(windows))
                .isExactlyInstanceOf(DomainValidationException.class)
                .hasMessageContaining("rolling window must not be null");
    }

    @Test
    void protectsItsWindowsFromMutation() {
        List<RollingWindow> suppliedWindows =
                new ArrayList<>(List.of(SEVEN_DAY_WINDOW, FOURTEEN_DAY_WINDOW));
        RollingWindows windows = RollingWindows.create(suppliedWindows);

        suppliedWindows.clear();

        assertThat(windows).containsExactly(SEVEN_DAY_WINDOW, FOURTEEN_DAY_WINDOW);
        assertThatThrownBy(windows::clear).isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void hasListValueSemantics() {
        RollingWindows first =
                RollingWindows.create(List.of(SEVEN_DAY_WINDOW, FOURTEEN_DAY_WINDOW));
        RollingWindows equal =
                RollingWindows.create(List.of(SEVEN_DAY_WINDOW, FOURTEEN_DAY_WINDOW));
        RollingWindows different = RollingWindows.create(List.of(SEVEN_DAY_WINDOW));

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isNotEqualTo(different)
                .isNotNull();
    }
}
