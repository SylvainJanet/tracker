export type WeightValidationError =
  { kind: 'number' } | { kind: 'positive' } | { kind: 'multiple-of-grams-unit'; gramsUnit: number };

export class Weight {
  private static readonly MEASUREMENT_INCREMENT_IN_GRAMS = 50;

  public static measurementIncrementInKilograms(): number {
    return Weight.kilogramsFromGrams(Weight.MEASUREMENT_INCREMENT_IN_GRAMS);
  }

  private static gramsFromKilograms(kilograms: number): number {
    const grams = kilograms * 1000;
    const nearestWholeGram = Math.round(grams);

    return nearestWholeGram / 1000 === kilograms ? nearestWholeGram : grams;
  }

  private static kilogramsFromGrams(grams: number): number {
    return grams / 1000;
  }

  private static validateWeightInGrams(grams: number): WeightValidationError[] {
    const errors: WeightValidationError[] = [];
    if (!Number.isSafeInteger(grams)) {
      errors.push({ kind: 'number' });
    }
    if (grams <= 0) {
      errors.push({ kind: 'positive' });
    }
    if (grams % Weight.MEASUREMENT_INCREMENT_IN_GRAMS !== 0) {
      errors.push({
        kind: 'multiple-of-grams-unit',
        gramsUnit: Weight.MEASUREMENT_INCREMENT_IN_GRAMS,
      });
    }
    return errors;
  }

  public static validateWeightInKilograms(kilograms: number): WeightValidationError[] {
    return Weight.validateWeightInGrams(Weight.gramsFromKilograms(kilograms));
  }

  private constructor(private readonly grams: number) {
    if (Weight.validateWeightInGrams(grams).length > 0) {
      throw new RangeError(
        `"${grams}" is not a valid weight in grams: must be a positive integer multiple of ${Weight.MEASUREMENT_INCREMENT_IN_GRAMS}`,
      );
    }
  }

  public static of(kilograms: number): Weight {
    const grams = Weight.gramsFromKilograms(kilograms);
    return new Weight(grams);
  }

  public inKilograms(): number {
    return this.grams / 1000;
  }
}
