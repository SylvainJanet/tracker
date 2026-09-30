package fr.sylvainjanet.tracker.technical.domain.contract.error;

import fr.sylvainjanet.tracker.technical.domain.contract.base.EqualityExplicit;
import fr.sylvainjanet.tracker.technical.domain.contract.base.StringRepresentationExplicit;
import java.util.Objects;

public abstract class DomainValidationErrorMetadata
        implements EqualityExplicit<DomainValidationErrorMetadata>,
                StringRepresentationExplicit<DomainValidationErrorMetadata> {

    protected final String publicMessage;

    protected DomainValidationErrorMetadata(String publicMessage) {
        this.publicMessage =
                Objects.requireNonNull(publicMessage, "public message must not be null");
    }

    protected String publicMessage() {
        return publicMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DomainValidationErrorMetadata that = (DomainValidationErrorMetadata) o;
        return Objects.equals(publicMessage, that.publicMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(publicMessage);
    }

    @Override
    public String toString() {
        return "DomainValidationErrorMetadata{" + "publicMessage='" + publicMessage + '\'' + '}';
    }
}
