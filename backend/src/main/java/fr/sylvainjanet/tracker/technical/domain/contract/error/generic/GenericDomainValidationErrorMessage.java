package fr.sylvainjanet.tracker.technical.domain.contract.error.generic;

import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationErrorMetadata;

public final class GenericDomainValidationErrorMessage extends DomainValidationErrorMetadata {

    private GenericDomainValidationErrorMessage(String publicMessage) {
        super(publicMessage);
    }

    public static GenericDomainValidationErrorMessage publicErrorMessage(String publicMessage) {
        return new GenericDomainValidationErrorMessage(publicMessage);
    }

    @Override
    public String toString() {
        return "GenericDomainValidationErrorMessage{"
                + "publicMessage='"
                + publicMessage
                + '\''
                + '}';
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
