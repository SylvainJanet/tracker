package fr.sylvainjanet.tracker.technical.domain.contract.generic;

import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainIdentifier;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.object.DomainEntityObject;

public interface DomainGenericEntityObject<T extends DomainGenericEntityObject<T>>
        extends DomainEntityObject<
                T,
                GenericDomainValidationErrorKind,
                GenericDomainValidationErrorMessage,
                GenericDomainIdentifier> {}
