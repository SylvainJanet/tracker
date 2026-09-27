package fr.sylvainjanet.tracker.technical.domain.contract.error.generic;

import static fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage.publicErrorMessage;

import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public final class GenericDomainValidationError
        extends DomainValidationError<
                GenericDomainValidationErrorKind, GenericDomainValidationErrorMessage> {

    private GenericDomainValidationError(
            GenericDomainValidationErrorKind kind, GenericDomainValidationErrorMessage metadata) {
        super(kind, metadata);
    }

    public static GenericDomainValidationError genericError(String message) {
        return new GenericDomainValidationError(
                GenericDomainValidationErrorKind.GENERIC_ERROR, publicErrorMessage(message));
    }

    @Override
    public @NonNull String toString() {
        return "GenericDomainValidationError["
                + "kind="
                + kind
                + ", "
                + "metadata="
                + metadata
                + ']';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GenericDomainValidationError) obj;
        return Objects.equals(this.kind, that.kind) && Objects.equals(this.metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, metadata);
    }
}
