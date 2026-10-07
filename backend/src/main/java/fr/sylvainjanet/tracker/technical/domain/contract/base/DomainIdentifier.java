package fr.sylvainjanet.tracker.technical.domain.contract.base;

import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationErrorMetadata;
import fr.sylvainjanet.tracker.technical.domain.contract.object.DomainValueObject;

public interface DomainIdentifier<
                I extends DomainIdentifier<I, K, M>,
                K extends Enum<K> & DomainEnum<K>,
                M extends DomainValidationErrorMetadata>
        extends DomainValueObject<I, K, M> {}
