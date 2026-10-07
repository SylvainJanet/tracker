package fr.sylvainjanet.tracker.technical.domain.contract.error;

import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainEnum;
import fr.sylvainjanet.tracker.technical.domain.contract.base.EqualityExplicit;
import fr.sylvainjanet.tracker.technical.domain.contract.base.StringRepresentationExplicit;
import java.util.Objects;

public abstract class DomainValidationError<
                K extends Enum<K> & DomainEnum<K>, M extends DomainValidationErrorMetadata>
        implements EqualityExplicit<DomainValidationError<K, M>>,
                StringRepresentationExplicit<DomainValidationError<K, M>> {

    protected final K kind;
    protected final M metadata;

    protected DomainValidationError(K kind, M metadata) {
        this.kind = Objects.requireNonNull(kind, "kind must not be null");
        this.metadata = Objects.requireNonNull(metadata, "metadata must not be null");
    }

    public K kind() {
        return kind;
    }

    public M metadata() {
        return metadata;
    }

    public String publicMessage() {
        return metadata.publicMessage();
    }

    @Override
    public String toString() {
        return "DomainValidationError[" + "kind=" + kind + ", " + "metadata=" + metadata + ']';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DomainValidationError<?, ?>) obj;
        return Objects.equals(this.kind, that.kind) && Objects.equals(this.metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, metadata);
    }
}
