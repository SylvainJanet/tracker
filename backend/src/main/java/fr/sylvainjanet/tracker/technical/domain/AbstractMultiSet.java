package fr.sylvainjanet.tracker.technical.domain;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jspecify.annotations.NonNull;

abstract class AbstractMultiSet<T> implements Iterable<T> {

    abstract Map<T, Integer> counts();

    abstract int totalSize();

    public final int count(T element) {
        Objects.requireNonNull(element, "element must not be null");
        return counts().getOrDefault(element, 0);
    }

    public final boolean contains(T element) {
        Objects.requireNonNull(element, "element must not be null");
        return counts().containsKey(element);
    }

    /**
     * Total number of occurrences, including duplicates.
     *
     * <p>{A, A, B}.size() == 3
     */
    public final int size() {
        return totalSize();
    }

    /**
     * Number of distinct values.
     *
     * <p>{A, A, B}.distinctSize() == 2
     */
    public final int distinctSize() {
        return counts().size();
    }

    public final boolean isEmpty() {
        return totalSize() == 0;
    }

    /** Returns an immutable snapshot of the distinct elements. */
    public final Set<T> distinctElements() {
        return Set.copyOf(counts().keySet());
    }

    /**
     * Iterates once per occurrence.
     *
     * <p>The iteration order is deliberately unspecified.
     */
    @Override
    public final @NonNull Iterator<T> iterator() {
        Iterator<Map.Entry<T, Integer>> entries = counts().entrySet().iterator();

        return new Iterator<>() {

            private Map.Entry<T, Integer> current;
            private int remaining;

            @Override
            public boolean hasNext() {
                return remaining > 0 || entries.hasNext();
            }

            @Override
            public T next() {
                if (remaining == 0) {
                    if (!entries.hasNext()) {
                        throw new NoSuchElementException();
                    }

                    current = entries.next();
                    remaining = current.getValue();
                }

                remaining--;
                return current.getKey();
            }
        };
    }

    public final Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public final Spliterator<T> spliterator() {
        return Spliterators.spliterator(
                iterator(), size(), Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.NONNULL);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof AbstractMultiSet<?> that)) {
            return false;
        }

        return counts().equals(that.counts());
    }

    @Override
    public int hashCode() {
        return counts().hashCode();
    }

    @Override
    public String toString() {
        return counts().toString();
    }
}
