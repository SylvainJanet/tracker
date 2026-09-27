package fr.sylvainjanet.tracker.statistics.domain.processor;

import static org.assertj.core.api.Assertions.assertThat;

import fr.sylvainjanet.tracker.statistics.domain.value.RollingWindow;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class WindowsSorterTest {

    private static final WindowsSorter SORTER = WindowsSorter.getInstance();

    @Test
    void sortsByAscendingWindowSizeWithoutDiscardingWindows() {
        RollingWindow fourteenDays = RollingWindow.create(14L);
        RollingWindow firstSevenDays = RollingWindow.create(7L);
        RollingWindow secondSevenDays = RollingWindow.create(7L);

        List<RollingWindow> sorted =
                SORTER.sortByIndex(List.of(fourteenDays, firstSevenDays, secondSevenDays));

        assertThat(sorted).extracting(RollingWindow::size).containsExactly(7L, 7L, 14L);
        assertThat(sorted).containsExactlyInAnyOrder(fourteenDays, firstSevenDays, secondSevenDays);
    }

    @Test
    void doesNotMutateTheSuppliedList() {
        RollingWindow fourteenDays = RollingWindow.create(14L);
        RollingWindow sevenDays = RollingWindow.create(7L);
        List<RollingWindow> suppliedWindows = new ArrayList<>(List.of(fourteenDays, sevenDays));

        SORTER.sortByIndex(suppliedWindows);

        assertThat(suppliedWindows).containsExactly(fourteenDays, sevenDays);
    }

    @Test
    void sortsAnEmptyList() {
        assertThat(SORTER.sortByIndex(List.of())).isEmpty();
    }
}
