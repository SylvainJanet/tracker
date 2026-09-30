package fr.sylvainjanet.tracker.architecturefixture.domain.contract;

import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainEnum;
import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainIdentifier;
import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainObject;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainIdentifier;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.generic.DomainGenericValueObject;
import java.util.Set;

public final class InvalidDomainContractTypes {

    private InvalidDomainContractTypes() {}

    public static final class NonEnumDomainEnum
            implements DomainEnum<ValidDomainContractTypes.ValidDomainEnum> {

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public enum WrongSelfDomainEnum
            implements DomainEnum<ValidDomainContractTypes.ValidDomainEnum> {
        VALUE;

        @Override
        public String toString() {
            return name();
        }
    }

    public static final class MissingEqualsValueObject
            implements DomainGenericValueObject<MissingEqualsValueObject> {

        @Override
        public Set<
                        DomainValidationError<
                                GenericDomainValidationErrorKind,
                                GenericDomainValidationErrorMessage>>
                validate() {
            return Set.of();
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

    public static final class MissingHashCodeValueObject
            implements DomainGenericValueObject<MissingHashCodeValueObject> {

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
        public String toString() {
            return super.toString();
        }
    }

    public static final class MissingToStringValueObject
            implements DomainGenericValueObject<MissingToStringValueObject> {

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
    }

    public static final class WrongSelfValueObject
            implements DomainGenericValueObject<ValidDomainContractTypes.ValidGenericValueObject> {

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

    public static final class WrongSelfIdentifier
            implements DomainIdentifier<
                    GenericDomainIdentifier,
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

    public static final class RawDomainObject
            implements DomainObject<
                    RawDomainObject,
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
}
