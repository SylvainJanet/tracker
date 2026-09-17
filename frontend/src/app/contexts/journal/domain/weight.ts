declare const weightBrand: unique symbol;

export type Weight = number & {
  readonly [weightBrand]: true;
};

export function weight(value: number): Weight {
  if (!isWeight(value)) {
    throw new RangeError(`"${value}" is not a valid weight: must be a positive number`);
  }

  return value;
}

export function isWeight(value: number): value is Weight {
  return value >= 0;
}

export function parseWeight(value: number): Weight | null {
  if (!isWeight(value)) {
    return null;
  }

  return value as Weight;
}
