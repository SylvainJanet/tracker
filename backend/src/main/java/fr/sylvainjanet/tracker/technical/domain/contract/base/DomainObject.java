package fr.sylvainjanet.tracker.technical.domain.contract.base;

import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationErrorMetadata;

public interface DomainObject<
                T extends DomainObject<T, K, M>,
                K extends Enum<K> & DomainEnum<K>,
                M extends DomainValidationErrorMetadata>
        extends EqualityExplicit<T>, StringRepresentationExplicit<T>, DomainValidated<T, K, M> {}
