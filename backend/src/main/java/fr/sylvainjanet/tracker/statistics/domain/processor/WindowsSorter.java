package fr.sylvainjanet.tracker.statistics.domain.processor;

import static java.util.Comparator.comparingLong;

import fr.sylvainjanet.tracker.statistics.domain.value.RollingWindow;
import java.util.List;

public final class WindowsSorter {

    private static WindowsSorter INSTANCE;

    private WindowsSorter() {}

    public static WindowsSorter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WindowsSorter();
        }
        return INSTANCE;
    }

    public List<RollingWindow> sortByIndex(List<RollingWindow> indexedValues) {
        return indexedValues.stream().sorted(comparingLong(RollingWindow::size)).toList();
    }
}
