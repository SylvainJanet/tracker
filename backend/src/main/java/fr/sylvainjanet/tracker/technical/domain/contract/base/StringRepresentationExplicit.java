package fr.sylvainjanet.tracker.technical.domain.contract.base;

public interface StringRepresentationExplicit<T extends StringRepresentationExplicit<T>> {

    @Override
    String toString();
}
