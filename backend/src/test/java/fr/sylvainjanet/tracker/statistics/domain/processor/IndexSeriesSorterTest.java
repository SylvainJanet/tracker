package fr.sylvainjanet.tracker.statistics.domain.processor;

import static org.assertj.core.api.Assertions.assertThat;

import fr.sylvainjanet.tracker.statistics.domain.value.IndexedValue;
import fr.sylvainjanet.tracker.statistics.domain.value.Value;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class IndexSeriesSorterTest {

    private static final IndexSeriesSorter SORTER = IndexSeriesSorter.getInstance();

    @Test
    void sortsByAscendingIndexWithoutDiscardingValues() {
        IndexedValue indexFour = IndexedValue.create(4L, Value.create(40));
        IndexedValue firstIndexOne = IndexedValue.create(1L, Value.create(10));
        IndexedValue secondIndexOne = IndexedValue.create(1L, Value.create(11));

        List<IndexedValue> sorted =
                SORTER.sortByIndex(List.of(indexFour, firstIndexOne, secondIndexOne));

        assertThat(sorted).extracting(IndexedValue::index).containsExactly(1L, 1L, 4L);
        assertThat(sorted).containsExactlyInAnyOrder(indexFour, firstIndexOne, secondIndexOne);
    }

    @Test
    void doesNotMutateTheSuppliedList() {
        IndexedValue indexFour = IndexedValue.create(4L, Value.create(40));
        IndexedValue indexOne = IndexedValue.create(1L, Value.create(10));
        List<IndexedValue> suppliedValues = new ArrayList<>(List.of(indexFour, indexOne));

        SORTER.sortByIndex(suppliedValues);

        assertThat(suppliedValues).containsExactly(indexFour, indexOne);
    }

    @Test
    void sortsAnEmptyList() {
        assertThat(SORTER.sortByIndex(List.of())).isEmpty();
    }
}
