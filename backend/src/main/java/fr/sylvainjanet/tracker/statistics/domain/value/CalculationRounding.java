package fr.sylvainjanet.tracker.statistics.domain.value;

import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainEnum;
import java.math.RoundingMode;
import java.util.Set;

public enum CalculationRounding implements DomainEnum<CalculationRounding> {
    PRECISE(20, RoundingMode.HALF_UP),
    PRETTY(2, RoundingMode.HALF_UP);

    private final int scale;
    private final RoundingMode roundingMode;

    CalculationRounding(int scale, RoundingMode roundingMode) {
        this.scale = scale;
        this.roundingMode = roundingMode;
    }

    public static Set<CalculationRounding> all() {
        return Set.of(CalculationRounding.values());
    }

    public int scale() {
        return scale;
    }

    public RoundingMode roundingMode() {
        return roundingMode;
    }

    @Override
    public String toString() {
        return "CalculationRounding"
                + "["
                + this.name()
                + "]"
                + "{"
                + "scale="
                + scale
                + ", roundingMode="
                + roundingMode
                + '}';
    }
}
