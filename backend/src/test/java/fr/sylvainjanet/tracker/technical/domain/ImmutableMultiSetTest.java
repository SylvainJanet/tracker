package fr.sylvainjanet.tracker.technical.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import org.junit.jupiter.api.Test;

class ImmutableMultiSetTest {

    @Test
    void createsAnEmptyImmutableMultiset() {
        ImmutableMultiSet<String> multiset = ImmutableMultiSet.of();

        assertThat(multiset.isEmpty()).isTrue();
        assertThat(multiset.size()).isZero();
        assertThat(multiset.distinctSize()).isZero();
        assertThat(multiset.distinctElements()).isEmpty();
        assertThat(multiset).isEmpty();
    }

    @Test
    void createsAnImmutableMultisetThatPreservesOccurrences() {
        ImmutableMultiSet<String> multiset = ImmutableMultiSet.of("apple", "pear", "apple");

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

        ImmutableMultiSet<String> multiset = ImmutableMultiSet.copyOf(suppliedElements);
        suppliedElements.clear();

        assertThat(multiset).containsExactlyInAnyOrder("apple", "apple", "pear");
    }

    @Test
    void reusesAnAlreadyImmutableMultiset() {
        ImmutableMultiSet<String> multiset = ImmutableMultiSet.of("apple", "apple", "pear");

        ImmutableMultiSet<String> copy = ImmutableMultiSet.copyOf(multiset);

        assertThat(copy).isSameAs(multiset);
    }

    @Test
    void protectedConstructorCopiesElementsForImmutableSubclasses() {
        List<String> suppliedElements = new ArrayList<>(List.of("apple", "apple", "pear"));

        TestImmutableMultiSet<String> multiset = new TestImmutableMultiSet<>(suppliedElements);
        suppliedElements.clear();

        assertThat(multiset).containsExactlyInAnyOrder("apple", "apple", "pear");
        assertThat(multiset.size()).isEqualTo(3);
    }

    @Test
    void internalCountFactoryCopiesItsInput() {
        Map<String, Integer> suppliedCounts = new HashMap<>();
        suppliedCounts.put("apple", 2);
        suppliedCounts.put("pear", 1);

        ImmutableMultiSet<String> multiset = ImmutableMultiSet.fromCounts(suppliedCounts, 3);
        suppliedCounts.clear();

        assertThat(multiset).containsExactlyInAnyOrder("apple", "apple", "pear");
        assertThat(multiset.size()).isEqualTo(3);
    }

    @Test
    void exposesImmutableDistinctElements() {
        ImmutableMultiSet<String> multiset = ImmutableMultiSet.of("apple", "apple", "pear");

        Set<String> distinctElements = multiset.distinctElements();

        assertThat(distinctElements).containsExactlyInAnyOrder("apple", "pear");
        assertThatThrownBy(distinctElements::clear)
                .isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void iteratesOncePerOccurrenceAndFailsAfterExhaustion() {
        ImmutableMultiSet<String> multiset = ImmutableMultiSet.of("apple");
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
        ImmutableMultiSet<String> multiset = ImmutableMultiSet.of("apple", "apple", "pear");

        assertThat(multiset.stream()).containsExactlyInAnyOrder("apple", "apple", "pear");

        Spliterator<String> spliterator = multiset.spliterator();
        assertThat(spliterator.getExactSizeIfKnown()).isEqualTo(3);
        assertThat(
                        spliterator.hasCharacteristics(
                                Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.NONNULL))
                .isTrue();
    }

    @Test
    void collectsSequentialAndParallelStreams() {
        List<String> elements = List.of("apple", "pear", "apple", "orange", "pear");

        ImmutableMultiSet<String> sequential =
                elements.stream().collect(ImmutableMultiSet.toImmutableMultiSet());
        ImmutableMultiSet<String> parallel =
                elements.parallelStream().collect(ImmutableMultiSet.toImmutableMultiSet());

        assertThat(sequential).containsExactlyInAnyOrderElementsOf(elements);
        assertThat(parallel).isEqualTo(sequential);
    }

    @Test
    void hasMultisetValueSemanticsAcrossImmutableAndMutableImplementations() {
        ImmutableMultiSet<String> first = ImmutableMultiSet.of("apple", "apple", "pear");
        ImmutableMultiSet<String> equal = ImmutableMultiSet.of("pear", "apple", "apple");
        ImmutableMultiSet<String> different = ImmutableMultiSet.of("apple", "pear");
        MultiSet<String> mutable = MultiSet.of("pear", "apple", "apple");

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isEqualTo(mutable)
                .hasSameHashCodeAs(mutable)
                .isNotEqualTo(different)
                .isNotEqualTo(List.of("apple", "apple", "pear"))
                .isNotNull();
        assertThat(mutable).isEqualTo(first);
        assertThat(ImmutableMultiSet.of("apple", "apple")).hasToString("{apple=2}");
    }

    @Test
    void rejectsNullsWhenConstructingAndQuerying() {
        assertThatThrownBy(() -> ImmutableMultiSet.of((String[]) null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("elements must not be null");
        assertThatThrownBy(() -> ImmutableMultiSet.of("apple", null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("element must not be null");
        assertThatThrownBy(() -> ImmutableMultiSet.copyOf(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("elements must not be null");
        assertThatThrownBy(() -> ImmutableMultiSet.copyOf(Arrays.asList("apple", null)))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("element must not be null");
        assertThatThrownBy(() -> new TestImmutableMultiSet<String>(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("elements must not be null");

        ImmutableMultiSet<String> multiset = ImmutableMultiSet.of("apple");
        assertThatThrownBy(() -> multiset.count(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("element must not be null");
        assertThatThrownBy(() -> multiset.contains(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("element must not be null");
    }

    private static final class TestImmutableMultiSet<T> extends ImmutableMultiSet<T> {

        private TestImmutableMultiSet(Iterable<? extends T> elements) {
            super(elements);
        }
    }
}
