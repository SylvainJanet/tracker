import { FormControl, type ValidationErrors } from '@angular/forms';
import { describe, expect, it } from 'vitest';

import { validWeight } from './weight.validator';

describe('validWeight', () => {
  it.each([null, undefined, ''])('leaves the empty value %s to the required validator', (value) => {
    expect(validate(value)).toBeNull();
  });

  it.each([72.5, '72.5'])('accepts the valid weight %s', (value) => {
    expect(validate(value)).toBeNull();
  });

  it('rejects values that cannot represent numbers', () => {
    expect(validate({})).toEqual({ notANumber: true });
  });

  it('maps invalid numeric text to its applicable errors', () => {
    expect(validate('not-a-number')).toEqual({
      notANumber: true,
      notMultipleOfGramsUnit: { gramsUnit: 50 },
    });
  });

  it.each([0, -5])('rejects the non-positive weight %s', (value) => {
    expect(validate(value)).toEqual({ notPositive: true });
  });

  it('reports the required weight increment', () => {
    expect(validate(72.51)).toEqual({
      notMultipleOfGramsUnit: { gramsUnit: 50 },
    });
  });

  it('returns every applicable validation error', () => {
    expect(validate(-12.34)).toEqual({
      notPositive: true,
      notMultipleOfGramsUnit: { gramsUnit: 50 },
    });
  });
});

function validate(value: unknown): ValidationErrors | null {
  return validWeight(new FormControl<unknown>(value));
}
