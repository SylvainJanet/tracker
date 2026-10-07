package fr.sylvainjanet.tracker.technical.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;

public class MultiSet<T> extends AbstractMultiSet<T> {

    private final Map<T, Integer> counts = new HashMap<>();
    private int size;

    @SafeVarargs
    public static <T> MultiSet<T> of(T... elements) {
        Objects.requireNonNull(elements, "elements must not be null");

        MultiSet<T> result = new MultiSet<>();

        for (T element : elements) {
            result.add(element);
        }

        return result;
    }

    public static <T> MultiSet<T> copyOf(Iterable<? extends T> elements) {
        Objects.requireNonNull(elements, "elements must not be null");

        MultiSet<T> result = new MultiSet<>();
        result.addAll(elements);
        return result;
    }

    public void add(T element) {
        add(element, 1);
    }

    public void add(T element, int occurrences) {
        Objects.requireNonNull(element, "element must not be null");

        if (occurrences <= 0) {
            throw new IllegalArgumentException("occurrences must be strictly positive");
        }

        int previous = counts.getOrDefault(element, 0);
        int updatedCount = Math.addExact(previous, occurrences);
        int updatedSize = Math.addExact(size, occurrences);

        counts.put(element, updatedCount);
        size = updatedSize;
    }

    public void addAll(Iterable<? extends T> elements) {
        Objects.requireNonNull(elements, "elements must not be null");

        for (T element : elements) {
            add(element);
        }
    }

    /**
     * Removes one occurrence.
     *
     * @return true when an occurrence existed and was removed
     */
    public boolean remove(T element) {
        return remove(element, 1) == 1;
    }

    /**
     * Removes up to {@code occurrences} occurrences.
     *
     * @return the actual number removed
     */
    public int remove(T element, int occurrences) {
        Objects.requireNonNull(element, "element must not be null");

        if (occurrences <= 0) {
            throw new IllegalArgumentException("occurrences must be strictly positive");
        }

        Integer current = counts.get(element);

        if (current == null) {
            return 0;
        }

        int removed = Math.min(current, occurrences);
        int remaining = current - removed;

        if (remaining == 0) {
            counts.remove(element);
        } else {
            counts.put(element, remaining);
        }

        size -= removed;
        return removed;
    }

    /** Removes every occurrence and returns how many existed. */
    public int removeAll(T element) {
        Objects.requireNonNull(element, "element must not be null");

        Integer removed = counts.remove(element);

        if (removed == null) {
            return 0;
        }

        size -= removed;
        return removed;
    }

    public void clear() {
        counts.clear();
        size = 0;
    }

    public ImmutableMultiSet<T> toImmutable() {
        return ImmutableMultiSet.fromCounts(counts, size);
    }

    public static <T> Collector<T, MultiSet<T>, MultiSet<T>> toMultiSet() {
        return Collector.of(
                MultiSet::new,
                MultiSet::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                });
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    Map<T, Integer> counts() {
        return counts;
    }

    @Override
    int totalSize() {
        return size;
    }
}
