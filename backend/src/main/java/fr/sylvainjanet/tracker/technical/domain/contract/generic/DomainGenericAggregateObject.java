package fr.sylvainjanet.tracker.technical.domain.contract.generic;

import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.object.DomainAggregateObject;

public interface DomainGenericAggregateObject<T extends DomainGenericAggregateObject<T>>
        extends DomainAggregateObject<
                T, GenericDomainValidationErrorKind, GenericDomainValidationErrorMessage> {}
