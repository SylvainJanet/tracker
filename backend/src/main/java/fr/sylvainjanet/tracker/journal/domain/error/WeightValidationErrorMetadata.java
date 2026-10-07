package fr.sylvainjanet.tracker.journal.domain.error;

import fr.sylvainjanet.tracker.technical.domain.contract.error.DomainValidationErrorMetadata;
import java.util.Objects;

public final class WeightValidationErrorMetadata extends DomainValidationErrorMetadata {

    private final Integer gramsUnit;

    WeightValidationErrorMetadata(String publicMessage, Integer gramsUnit) {
        super(publicMessage);
        this.gramsUnit = gramsUnit;
    }

    public Integer gramsUnit() {
        return gramsUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WeightValidationErrorMetadata that = (WeightValidationErrorMetadata) o;
        return Objects.equals(gramsUnit, that.gramsUnit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gramsUnit);
    }

    @Override
    public String toString() {
        return "WeightValidationErrorMetadata{"
                + "gramsUnit="
                + gramsUnit
                + ", publicMessage='"
                + publicMessage
                + '\''
                + '}';
    }
}
