package fr.sylvainjanet.tracker.technical.domain.contract.error.generic;

import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainEnum;

public enum GenericDomainValidationErrorKind
        implements DomainEnum<GenericDomainValidationErrorKind> {
    GENERIC_ERROR;

    @Override
    public String toString() {
        return super.toString();
    }
}
