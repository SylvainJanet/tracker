package fr.sylvainjanet.tracker.technical.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import org.junit.jupiter.api.Test;

class MultiSetTest {

    @Test
    void createsAnEmptyMultiset() {
        MultiSet<String> multiset = new MultiSet<>();

        assertThat(multiset.isEmpty()).isTrue();
        assertThat(multiset.size()).isZero();
        assertThat(multiset.distinctSize()).isZero();
        assertThat(multiset.distinctElements()).isEmpty();
        assertThat(multiset).isEmpty();
    }

    @Test
    void createsAMultisetThatPreservesOccurrences() {
        MultiSet<String> multiset = MultiSet.of("apple", "pear", "apple");

        assertThat(multiset).containsExactlyInAnyOrder("apple", "apple", "pear");
        assertThat(multiset.size()).isEqualTo(3);
        assertThat(multiset.distinctSize()).isEqualTo(2);
        assertThat(multiset.count("apple")).isEqualTo(2);
        assertThat(multiset.count("pear")).isOne();
        assertThat(multiset.count("missing")).isZero();
        assertThat(multiset.contains("apple")).isTrue();
        assertThat(multiset.contains("missing")).isFalse();
        assertThat(multiset.isEmpty()).isFalse();
        assertThat(multiset.distinctElements()).containsExactlyInAnyOrder("apple", "pear");
    }

    @Test
    void copiesAnIterableWithoutRetainingItsMutableState() {
        List<String> suppliedElements = new ArrayList<>(List.of("apple", "apple", "pear"));

        MultiSet<String> multiset = MultiSet.copyOf(suppliedElements);
        suppliedElements.clear();

        assertThat(multiset).containsExactlyInAnyOrder("apple", "apple", "pear");
    }

    @Test
    void addsSingleAndMultipleOccurrences() {
        MultiSet<String> multiset = new MultiSet<>();

        multiset.add("apple");
        multiset.add("pear", 2);
        multiset.addAll(List.of("apple", "orange"));

        assertThat(multiset).containsExactlyInAnyOrder("apple", "apple", "pear", "pear", "orange");
        assertThat(multiset.size()).isEqualTo(5);
        assertThat(multiset.distinctSize()).isEqualTo(3);
    }

    @Test
    void rejectsInvalidElementsAndOccurrenceCountsWhenAdding() {
        MultiSet<String> multiset = new MultiSet<>();

        assertThatThrownBy(() -> multiset.add(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("element must not be null");
        assertThatThrownBy(() -> multiset.add("apple", 0))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("occurrences must be strictly positive");
        assertThatThrownBy(() -> multiset.add("apple", -1))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("occurrences must be strictly positive");
        assertThatThrownBy(() -> multiset.addAll(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("elements must not be null");
        assertThatThrownBy(() -> multiset.addAll(Arrays.asList("apple", null)))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("element must not be null");
    }

    @Test
    void remainsUnchangedWhenAnAdditionOverflows() {
        MultiSet<String> multiset = new MultiSet<>();
        multiset.add("apple", Integer.MAX_VALUE);

        assertThatThrownBy(() -> multiset.add("apple"))
                .isExactlyInstanceOf(ArithmeticException.class);
        assertThatThrownBy(() -> multiset.add("pear"))
                .isExactlyInstanceOf(ArithmeticException.class);

        assertThat(multiset.size()).isEqualTo(Integer.MAX_VALUE);
        assertThat(multiset.count("apple")).isEqualTo(Integer.MAX_VALUE);
        assertThat(multiset.contains("pear")).isFalse();
    }

    @Test
    void removesAvailableOccurrencesAndReportsTheActualAmountRemoved() {
        MultiSet<String> multiset = MultiSet.of("apple", "apple", "apple", "pear");

        assertThat(multiset.remove("apple")).isTrue();
        assertThat(multiset.count("apple")).isEqualTo(2);
        assertThat(multiset.remove("apple", 5)).isEqualTo(2);
        assertThat(multiset.contains("apple")).isFalse();
        assertThat(multiset.remove("missing")).isFalse();
        assertThat(multiset.remove("missing", 2)).isZero();
        assertThat(multiset).containsExactly("pear");
        assertThat(multiset.size()).isOne();
        assertThat(multiset.distinctSize()).isOne();
    }

    @Test
    void rejectsInvalidElementsAndOccurrenceCountsWhenRemoving() {
        MultiSet<String> multiset = MultiSet.of("apple");

        assertThatThrownBy(() -> multiset.remove(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("element must not be null");
        assertThatThrownBy(() -> multiset.remove("apple", 0))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("occurrences must be strictly positive");
        assertThatThrownBy(() -> multiset.remove("apple", -1))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("occurrences must be strictly positive");
        assertThatThrownBy(() -> multiset.removeAll(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("element must not be null");
    }

    @Test
    void removesAllOccurrencesAndCanBeCleared() {
        MultiSet<String> multiset = MultiSet.of("apple", "apple", "apple", "pear");

        assertThat(multiset.removeAll("apple")).isEqualTo(3);
        assertThat(multiset.removeAll("apple")).isZero();
        assertThat(multiset).containsExactly("pear");

        multiset.clear();

        assertThat(multiset).isEmpty();
        assertThat(multiset.size()).isZero();
        assertThat(multiset.distinctSize()).isZero();
    }

    @Test
    void exposesAnImmutableSnapshotOfDistinctElements() {
        MultiSet<String> multiset = MultiSet.of("apple", "apple", "pear");

        Set<String> distinctElements = multiset.distinctElements();
        multiset.add("orange");

        assertThat(distinctElements).containsExactlyInAnyOrder("apple", "pear");
        assertThatThrownBy(distinctElements::clear)
                .isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void iteratesOncePerOccurrenceAndFailsAfterExhaustion() {
        MultiSet<String> multiset = MultiSet.of("apple");
        Iterator<String> iterator = multiset.iterator();

        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEqualTo("apple");
        assertThat(iterator.hasNext()).isFalse();
        assertThatThrownBy(iterator::next).isExactlyInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(iterator::remove)
                .isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void streamsEveryOccurrenceAndReportsItsSpliteratorCharacteristics() {
        MultiSet<String> multiset = MultiSet.of("apple", "apple", "pear");

        assertThat(multiset.stream()).containsExactlyInAnyOrder("apple", "apple", "pear");

        Spliterator<String> spliterator = multiset.spliterator();
        assertThat(spliterator.getExactSizeIfKnown()).isEqualTo(3);
        assertThat(
                        spliterator.hasCharacteristics(
                                Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.NONNULL))
                .isTrue();
    }

    @Test
    void createsAnImmutableSnapshot() {
        MultiSet<String> multiset = MultiSet.of("apple", "apple", "pear");

        ImmutableMultiSet<String> snapshot = multiset.toImmutable();
        multiset.clear();

        assertThat(snapshot).containsExactlyInAnyOrder("apple", "apple", "pear");
        assertThat(snapshot.size()).isEqualTo(3);
        assertThat(snapshot.count("apple")).isEqualTo(2);
    }

    @Test
    void collectsSequentialAndParallelStreams() {
        List<String> elements = List.of("apple", "pear", "apple", "orange", "pear");

        MultiSet<String> sequential = elements.stream().collect(MultiSet.toMultiSet());
        MultiSet<String> parallel = elements.parallelStream().collect(MultiSet.toMultiSet());

        assertThat(sequential).containsExactlyInAnyOrderElementsOf(elements);
        assertThat(parallel).isEqualTo(sequential);
    }

    @Test
    void hasMultisetValueSemanticsAcrossMutableAndImmutableImplementations() {
        MultiSet<String> first = MultiSet.of("apple", "apple", "pear");
        MultiSet<String> equal = MultiSet.of("pear", "apple", "apple");
        MultiSet<String> different = MultiSet.of("apple", "pear");
        ImmutableMultiSet<String> immutable = first.toImmutable();

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isEqualTo(immutable)
                .hasSameHashCodeAs(immutable)
                .isNotEqualTo(different)
                .isNotEqualTo(List.of("apple", "apple", "pear"))
                .isNotNull();
        assertThat(immutable).isEqualTo(first);
        assertThat(MultiSet.of("apple", "apple")).hasToString("{apple=2}");
    }

    @Test
    void rejectsNullsWhenConstructingAndQuerying() {
        assertThatThrownBy(() -> MultiSet.of((String[]) null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("elements must not be null");
        assertThatThrownBy(() -> MultiSet.of("apple", null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("element must not be null");
        assertThatThrownBy(() -> MultiSet.copyOf(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("elements must not be null");
        assertThatThrownBy(() -> MultiSet.copyOf(Arrays.asList("apple", null)))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("element must not be null");

        MultiSet<String> multiset = MultiSet.of("apple");
        assertThatThrownBy(() -> multiset.count(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("element must not be null");
        assertThatThrownBy(() -> multiset.contains(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("element must not be null");
    }
}
