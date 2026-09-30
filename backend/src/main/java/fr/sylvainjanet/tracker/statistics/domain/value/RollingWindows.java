package fr.sylvainjanet.tracker.statistics.domain.value;

import static fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationError.genericError;

import fr.sylvainjanet.tracker.technical.domain.UnmodifiableList;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidator;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.generic.DomainGenericValueObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class RollingWindows extends UnmodifiableList<RollingWindow>
        implements DomainGenericValueObject<RollingWindows> {

    private RollingWindows(List<RollingWindow> windows) {
        super(windows);
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

        RollingWindow previousWindow = null;
        for (RollingWindow window : this) {
            if (window == null) {
                errors.add(genericError("rolling window must not be null"));
                continue;
            }
            if (previousWindow != null && window.size() <= previousWindow.size()) {
                errors.add(
                        genericError("rolling windows must be ordered by unique ascending sizes"));
            }
            previousWindow = window;
        }

        return errors;
    }

    public static RollingWindows create(List<RollingWindow> windows) {
        return new RollingWindows(windows);
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other);
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
