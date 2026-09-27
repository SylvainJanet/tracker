package fr.sylvainjanet.tracker.technical.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.junit.jupiter.api.Test;

class UnmodifiableListTest {

    @Test
    void preservesOrderedElementsAndSupportsIndexedAccess() {
        TestUnmodifiableList<String> list =
                new TestUnmodifiableList<>(List.of("apple", "pear", "orange"));

        assertThat(list).containsExactly("apple", "pear", "orange");
        assertThat(list.size()).isEqualTo(3);
        assertThat(list.isEmpty()).isFalse();
        assertThat(list.get(0)).isEqualTo("apple");
        assertThat(list.get(2)).isEqualTo("orange");
    }

    @Test
    void supportsAnEmptyList() {
        TestUnmodifiableList<String> list = new TestUnmodifiableList<>(List.of());

        assertThat(list).isEmpty();
        assertThat(list.size()).isZero();
    }

    @Test
    void copiesItsInputList() {
        List<String> suppliedElements = new ArrayList<>(List.of("apple", "pear"));

        TestUnmodifiableList<String> list = new TestUnmodifiableList<>(suppliedElements);
        suppliedElements.clear();

        assertThat(list).containsExactly("apple", "pear");
    }

    @Test
    void rejectsEveryListMutation() {
        TestUnmodifiableList<String> list = new TestUnmodifiableList<>(List.of("apple", "pear"));

        assertThatThrownBy(() -> list.add("orange"))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> list.add(1, "orange"))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> list.set(0, "orange"))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> list.remove(0))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> list.remove("apple"))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(list::clear).isExactlyInstanceOf(UnsupportedOperationException.class);

        assertThat(list).containsExactly("apple", "pear");
    }

    @Test
    void rejectsMutationThroughIteratorsAndSublists() {
        TestUnmodifiableList<String> list = new TestUnmodifiableList<>(List.of("apple", "pear"));

        Iterator<String> iterator = list.iterator();
        iterator.next();
        assertThatThrownBy(iterator::remove)
                .isExactlyInstanceOf(UnsupportedOperationException.class);

        ListIterator<String> listIterator = list.listIterator();
        listIterator.next();
        assertThatThrownBy(() -> listIterator.set("orange"))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> listIterator.add("orange"))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> list.subList(0, 1).clear())
                .isExactlyInstanceOf(UnsupportedOperationException.class);

        assertThat(list).containsExactly("apple", "pear");
    }

    @Test
    void followsTheListEqualityAndHashCodeContractAcrossImplementations() {
        TestUnmodifiableList<String> first = new TestUnmodifiableList<>(List.of("apple", "pear"));
        TestUnmodifiableList<String> equal = new TestUnmodifiableList<>(List.of("apple", "pear"));
        OtherUnmodifiableList<String> otherSubclass =
                new OtherUnmodifiableList<>(List.of("apple", "pear"));
        List<String> standardList = List.of("apple", "pear");
        TestUnmodifiableList<String> different =
                new TestUnmodifiableList<>(List.of("pear", "apple"));

        assertThat(first)
                .isEqualTo(equal)
                .hasSameHashCodeAs(equal)
                .isEqualTo(otherSubclass)
                .hasSameHashCodeAs(otherSubclass)
                .isEqualTo(standardList)
                .hasSameHashCodeAs(standardList)
                .isNotEqualTo(different)
                .isNotEqualTo(null);
        assertThat(standardList).isEqualTo(first);
        assertThat(otherSubclass).isEqualTo(first);
    }

    @Test
    void hasAnExplicitStringRepresentation() {
        TestUnmodifiableList<String> list = new TestUnmodifiableList<>(List.of("apple", "pear"));

        assertThat(list.toString()).isEqualTo("UnmodifiableList{list=[apple, pear]}");
    }

    @Test
    void supportsNullElementsButRejectsANullInputList() {
        TestUnmodifiableList<String> list =
                new TestUnmodifiableList<>(Arrays.asList("apple", null));

        assertThat(list).containsExactly("apple", null);
        assertThatThrownBy(() -> new TestUnmodifiableList<String>(null))
                .isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsIndexesOutsideItsBounds() {
        TestUnmodifiableList<String> list = new TestUnmodifiableList<>(List.of("apple"));

        assertThatThrownBy(() -> list.get(-1)).isExactlyInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> list.get(1)).isExactlyInstanceOf(IndexOutOfBoundsException.class);
    }

    private static final class TestUnmodifiableList<T> extends UnmodifiableList<T> {

        private TestUnmodifiableList(List<T> list) {
            super(list);
        }
    }

    private static final class OtherUnmodifiableList<T> extends UnmodifiableList<T> {

        private OtherUnmodifiableList(List<T> list) {
            super(list);
        }
    }
}
