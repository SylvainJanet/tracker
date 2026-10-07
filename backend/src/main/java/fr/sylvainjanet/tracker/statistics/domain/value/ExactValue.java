package fr.sylvainjanet.tracker.statistics.domain.value;

public sealed interface ExactValue permits Fraction {
    Approximation approximate(CalculationRounding rounding);
}
