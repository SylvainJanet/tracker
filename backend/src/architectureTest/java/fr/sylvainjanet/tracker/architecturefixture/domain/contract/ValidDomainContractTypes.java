package fr.sylvainjanet.tracker.architecturefixture.domain.contract;

import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainEnum;
import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainIdentifier;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainIdentifier;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.generic.DomainGenericAggregateObject;
import fr.sylvainjanet.tracker.technical.domain.contract.generic.DomainGenericEntityObject;
import fr.sylvainjanet.tracker.technical.domain.contract.generic.DomainGenericValueObject;
import fr.sylvainjanet.tracker.technical.domain.contract.object.DomainAggregateObject;
import fr.sylvainjanet.tracker.technical.domain.contract.object.DomainEntityObject;
import fr.sylvainjanet.tracker.technical.domain.contract.object.DomainValueObject;
import java.util.Set;

public final class ValidDomainContractTypes {

    private ValidDomainContractTypes() {}

    public static final class ValidValueObject
            implements DomainValueObject<
                    ValidValueObject,
                    GenericDomainValidationErrorKind,
                    GenericDomainValidationErrorMessage> {

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

    public static final class ValidGenericValueObject
            implements DomainGenericValueObject<ValidGenericValueObject> {

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

    public static final class ValidIdentifier
            implements DomainIdentifier<
                    ValidIdentifier,
                    GenericDomainValidationErrorKind,
                    GenericDomainValidationErrorMessage> {

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

    public static final class ValidEntityObject
            implements DomainEntityObject<
                    ValidEntityObject,
                    GenericDomainValidationErrorKind,
                    GenericDomainValidationErrorMessage,
                    GenericDomainIdentifier> {

        @Override
        public GenericDomainIdentifier identifier() {
            return new GenericDomainIdentifier(1L);
        }

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

    public static final class ValidGenericEntityObject
            implements DomainGenericEntityObject<ValidGenericEntityObject> {

        @Override
        public GenericDomainIdentifier identifier() {
            return new GenericDomainIdentifier(1L);
        }

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

    public static final class ValidAggregateObject
            implements DomainAggregateObject<
                    ValidAggregateObject,
                    GenericDomainValidationErrorKind,
                    GenericDomainValidationErrorMessage> {

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

    public static final class ValidGenericAggregateObject
            implements DomainGenericAggregateObject<ValidGenericAggregateObject> {

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

    public enum ValidDomainEnum implements DomainEnum<ValidDomainEnum> {
        VALUE;

        @Override
        public String toString() {
            return name();
        }
    }
}
