package fr.sylvainjanet.tracker.technical.domain.contract.object;

import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainEnum;
import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainIdentifier;
import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainObject;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationErrorMetadata;

public interface DomainEntityObject<
                T extends DomainEntityObject<T, K, M, I>,
                K extends Enum<K> & DomainEnum<K>,
                M extends DomainValidationErrorMetadata,
                I extends DomainIdentifier<I, K, M>>
        extends DomainObject<T, K, M> {
    I identifier();
}
