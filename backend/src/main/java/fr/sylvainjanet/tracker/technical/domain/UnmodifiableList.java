package fr.sylvainjanet.tracker.technical.domain;

import static java.util.Collections.unmodifiableList;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public abstract class UnmodifiableList<T> extends AbstractList<T> {

    private final List<T> list;

    protected UnmodifiableList(List<T> list) {
        this.list = unmodifiableList(new ArrayList<>(list));
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
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
        return "UnmodifiableList{" + "list=" + list + '}';
    }
}
