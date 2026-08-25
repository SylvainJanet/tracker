package fr.sylvainjanet.tracker.architecturefixture.domain;

import org.springframework.beans.factory.annotation.Autowired;

public final class FieldInjectedType {

    @Autowired private Object dependency;
}
