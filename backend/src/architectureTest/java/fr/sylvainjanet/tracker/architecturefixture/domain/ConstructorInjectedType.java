package fr.sylvainjanet.tracker.architecturefixture.domain;

public final class ConstructorInjectedType {

    private final Object dependency;

    public ConstructorInjectedType(Object dependency) {
        this.dependency = dependency;
    }
}
