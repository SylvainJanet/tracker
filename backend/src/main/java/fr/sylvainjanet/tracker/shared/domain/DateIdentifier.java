package fr.sylvainjanet.tracker.shared.domain;

import static fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationError.genericError;

import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainIdentifier;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidator;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DateIdentifier
        implements DomainIdentifier<
                DateIdentifier,
                GenericDomainValidationErrorKind,
                GenericDomainValidationErrorMessage> {
    private final LocalDate localDate;

    private DateIdentifier(LocalDate localDate) {
        this.localDate = localDate;
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

        if (localDate == null) {
            errors.add(genericError("Date cannot be null"));
        }

        return errors;
    }

    public static DateIdentifier create(LocalDate localDate) {
        return new DateIdentifier(localDate);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DateIdentifier dateIdentifier1 = (DateIdentifier) o;
        return Objects.equals(localDate, dateIdentifier1.localDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(localDate);
    }

    @Override
    public String toString() {
        return "DateIdentifier{" + "localDate=" + localDate + '}';
    }
}
