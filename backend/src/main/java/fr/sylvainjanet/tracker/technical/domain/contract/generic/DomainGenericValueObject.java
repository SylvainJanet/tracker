package fr.sylvainjanet.tracker.technical.domain.contract.generic;

import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.object.DomainValueObject;

public interface DomainGenericValueObject<T extends DomainGenericValueObject<T>>
        extends DomainValueObject<
                T, GenericDomainValidationErrorKind, GenericDomainValidationErrorMessage> {}
