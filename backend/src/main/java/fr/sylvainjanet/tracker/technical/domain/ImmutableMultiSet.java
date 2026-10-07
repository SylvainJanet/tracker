package fr.sylvainjanet.tracker.technical.domain;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;

public class ImmutableMultiSet<T> extends AbstractMultiSet<T> {

    private final Map<T, Integer> counts;
    private final int size;

    private ImmutableMultiSet(Map<? extends T, Integer> counts, int size) {
        this.counts = Map.copyOf(counts);
        this.size = size;
    }

    protected ImmutableMultiSet(Iterable<? extends T> elements) {
        MultiSet<T> snapshot = MultiSet.copyOf(elements);
        this.counts = Map.copyOf(snapshot.counts());
        this.size = snapshot.totalSize();
    }

    @SafeVarargs
    public static <T> ImmutableMultiSet<T> of(T... elements) {
        Objects.requireNonNull(elements, "elements must not be null");

        MultiSet<T> mutable = new MultiSet<>();

        for (T element : elements) {
            mutable.add(element);
        }

        return mutable.toImmutable();
    }

    public static <T> ImmutableMultiSet<T> copyOf(Iterable<? extends T> elements) {
        Objects.requireNonNull(elements, "elements must not be null");

        if (elements instanceof ImmutableMultiSet<?> immutable) {
            @SuppressWarnings("unchecked")
            ImmutableMultiSet<T> same = (ImmutableMultiSet<T>) immutable;

            return same;
        }

        MultiSet<T> mutable = MultiSet.copyOf(elements);
        return mutable.toImmutable();
    }

    static <T> ImmutableMultiSet<T> fromCounts(Map<? extends T, Integer> counts, int size) {
        return new ImmutableMultiSet<>(counts, size);
    }

    public static <T> Collector<T, MultiSet<T>, ImmutableMultiSet<T>> toImmutableMultiSet() {
        return Collector.of(
                MultiSet::new,
                MultiSet::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                MultiSet::toImmutable);
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
