package fr.sylvainjanet.tracker.technical.domain.contract.error.generic;

import static fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationError.genericError;

import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainIdentifier;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class GenericDomainIdentifier
        implements DomainIdentifier<
                GenericDomainIdentifier,
                GenericDomainValidationErrorKind,
                GenericDomainValidationErrorMessage> {

    private final long id;

    public GenericDomainIdentifier(long id) {
        this.id = id;
        DomainValidator.validate(this);
    }

    @Override
    public Set<
                    DomainValidationError<
                            GenericDomainValidationErrorKind, GenericDomainValidationErrorMessage>>
            validate() {
        Set<
                        DomainValidationError<
                                GenericDomainValidationErrorKind,
                                GenericDomainValidationErrorMessage>>
                errors = new HashSet<>();

        if (id <= 0) {
            errors.add(genericError("id must be positive"));
        }

        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GenericDomainIdentifier that = (GenericDomainIdentifier) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "GenericDomainIdentifier{" + "id=" + id + '}';
    }
}
