package fr.sylvainjanet.tracker.technical.domain.contract.object;

import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainEnum;
import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainObject;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationErrorMetadata;

public interface DomainValueObject<
                T extends DomainValueObject<T, K, M>,
                K extends Enum<K> & DomainEnum<K>,
                M extends DomainValidationErrorMetadata>
        extends DomainObject<T, K, M> {}
