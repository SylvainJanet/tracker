package fr.sylvainjanet.tracker.technical.domain.contract.base;

import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationErrorMetadata;
import java.util.Set;

public interface DomainValidated<
        T extends DomainValidated<T, K, M>,
        K extends Enum<K> & DomainEnum<K>,
        M extends DomainValidationErrorMetadata> {

    Set<DomainValidationError<K, M>> validate();
}
