export type WeightValidationError =
  { kind: 'number' } | { kind: 'positive' } | { kind: 'multiple-of-grams-unit'; gramsUnit: number };

export class Weight {
  private static readonly GRAMS_UNIT = 50;

  private static gramsFromKilograms(kilograms: number): number {
    const grams = kilograms * 1000;
    const nearestWholeGram = Math.round(grams);

    return nearestWholeGram / 1000 === kilograms ? nearestWholeGram : grams;
  }

  private static validateWeightInGrams(grams: number): WeightValidationError[] {
    const errors: WeightValidationError[] = [];
    if (!Number.isSafeInteger(grams)) {
      errors.push({ kind: 'number' });
    }
    if (grams <= 0) {
      errors.push({ kind: 'positive' });
    }
    if (grams % Weight.GRAMS_UNIT !== 0) {
      errors.push({ kind: 'multiple-of-grams-unit', gramsUnit: Weight.GRAMS_UNIT });
    }
    return errors;
  }

  public static validateWeightInKilograms(kilograms: number): WeightValidationError[] {
    return Weight.validateWeightInGrams(Weight.gramsFromKilograms(kilograms));
  }

  private constructor(private readonly grams: number) {
    if (Weight.validateWeightInGrams(grams).length > 0) {
      throw new RangeError(
        `"${grams}" is not a valid weight in grams: must be a positive integer multiple of ${Weight.GRAMS_UNIT}`,
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
