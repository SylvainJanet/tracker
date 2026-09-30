package fr.sylvainjanet.tracker.architecturefixture.application.service;

import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.generic.DomainGenericValueObject;
import java.util.Set;

public final class MisplacedDomainContractTypes {

    private MisplacedDomainContractTypes() {}

    public static final class MisplacedDomainValueObject
            implements DomainGenericValueObject<MisplacedDomainValueObject> {

        @Override
        public Set<
                        DomainValidationError<
                                GenericDomainValidationErrorKind,
                                GenericDomainValidationErrorMessage>>
                validate() {
            return Set.of();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
