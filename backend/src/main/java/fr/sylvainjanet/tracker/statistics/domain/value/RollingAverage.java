package fr.sylvainjanet.tracker.statistics.domain.value;

import static fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationError.genericError;

import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationError;
import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidator;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorKind;
import fr.sylvainjanet.tracker.technical.domain.contract.error.generic.GenericDomainValidationErrorMessage;
import fr.sylvainjanet.tracker.technical.domain.contract.generic.DomainGenericValueObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class RollingAverage implements DomainGenericValueObject<RollingAverage> {

    private final RollingWindow window;
    private final List<RollingAveragePoint> points;

    private RollingAverage(RollingWindow window, List<RollingAveragePoint> points) {
        this.window = window;
        this.points = points == null ? null : Collections.unmodifiableList(new ArrayList<>(points));
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

        if (window == null) {
            errors.add(genericError("Rolling window must not be null"));
        }
        if (points == null) {
            errors.add(genericError("Rolling average points must not be null"));
        } else {
            RollingAveragePoint previousValue = null;
            for (RollingAveragePoint point : points) {
                if (point == null) {
                    errors.add(genericError("Rolling average point must not be null"));
                } else if (previousValue != null && point.index() <= previousValue.index()) {
                    errors.add(
                            genericError(
                                    "Rolling average points must be ordered by unique ascending indexes"));
                }
                previousValue = point;
            }
        }

        return errors;
    }

    public static RollingAverage create(RollingWindow window, List<RollingAveragePoint> points) {
        return new RollingAverage(window, points);
    }

    public long windowSize() {
        return window.size();
    }

    public RollingWindow window() {
        return window;
    }

    public List<RollingAveragePoint> points() {
        return points;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RollingAverage) obj;
        return Objects.equals(this.window, that.window) && Objects.equals(this.points, that.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(window, points);
    }

    @Override
    public String toString() {
        return "RollingAverage[" + "window=" + window + ", " + "points=" + points + ']';
    }
}
