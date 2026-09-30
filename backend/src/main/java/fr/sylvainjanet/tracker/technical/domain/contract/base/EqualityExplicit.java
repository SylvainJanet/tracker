package fr.sylvainjanet.tracker.technical.domain.contract.base;

public interface EqualityExplicit<T extends EqualityExplicit<T>> {

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();
}
