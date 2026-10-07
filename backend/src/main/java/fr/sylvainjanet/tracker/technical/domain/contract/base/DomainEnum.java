package fr.sylvainjanet.tracker.technical.domain.contract.base;

public interface DomainEnum<T extends Enum<T> & DomainEnum<T>>
        extends StringRepresentationExplicit<T> {}
