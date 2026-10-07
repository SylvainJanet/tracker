package fr.sylvainjanet.tracker.journal.domain.error;

import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainEnum;

public enum WeightValidationErrorKind implements DomainEnum<WeightValidationErrorKind> {
    POSITIVE,
    MULTIPLE_OF_GRAMS_UNIT,
    INTEGER;

    @Override
    public String toString() {
        return this.name();
    }
}
