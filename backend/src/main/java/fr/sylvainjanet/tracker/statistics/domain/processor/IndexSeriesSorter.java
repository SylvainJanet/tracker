package fr.sylvainjanet.tracker.statistics.domain.processor;

import static java.util.Comparator.comparingLong;

import fr.sylvainjanet.tracker.statistics.domain.value.IndexedValue;
import java.util.List;

public final class IndexSeriesSorter {

    private static IndexSeriesSorter INSTANCE;

    private IndexSeriesSorter() {}

    public static IndexSeriesSorter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new IndexSeriesSorter();
        }
        return INSTANCE;
    }

    public List<IndexedValue> sortByIndex(List<IndexedValue> indexedValues) {
        return indexedValues.stream().sorted(comparingLong(IndexedValue::index)).toList();
    }
}
