import { describe, expect, it } from 'vitest';

import { Weight } from './weight';

describe('Weight', () => {
  describe('measurementIncrementInKilograms', () => {
    it('returns the supported measurement increment in kilograms', () => {
      expect(Weight.measurementIncrementInKilograms()).toBe(0.05);
    });
  });
  describe('Weight.of function', () => {
    it('accepts a valid weight', () => {
      expect(Weight.of(70)).toEqual({
        grams: 70000,
      });
    });

    it('accepts decimal kilograms representing a 50 gram increment but having inexact binary floating-point representation', () => {
      expect(Weight.of(64.1)).toEqual({
        grams: 64100,
      });
    });

    it('throws when constructing a non positive weight', () => {
      expect(() => Weight.of(-5)).toThrow(
        '"-5000" is not a valid weight in grams: must be a positive integer multiple of 50',
      );
    });

    it('throws when constructing an invalid weight', () => {
      expect(() => Weight.of(12.34)).toThrow(
        '"12340" is not a valid weight in grams: must be a positive integer multiple of 50',
      );
    });

    it('rejects 0', () => {
      expect(() => Weight.of(0)).toThrow(
        '"0" is not a valid weight in grams: must be a positive integer multiple of 50',
      );
    });

    it('returns its value in kilograms', () => {
      expect(Weight.of(64.1).inKilograms()).toBe(64.1);
    });
  });
  describe('Weight.validateWeightInKilograms function', () => {
    it('validates a valid weight', () => {
      expect(Weight.validateWeightInKilograms(70)).toEqual([]);
    });

    it('validates decimal kilograms representing a 50 gram increment but having inexact binary floating-point representation', () => {
      expect(Weight.validateWeightInKilograms(64.1)).toEqual([]);
    });

    it('invalidates a non positive weight', () => {
      expect(Weight.validateWeightInKilograms(-5)).toEqual([{ kind: 'positive' }]);
    });

    it('invalidates a weight not in increment', () => {
      expect(Weight.validateWeightInKilograms(12.34)).toEqual([
        { kind: 'multiple-of-grams-unit', gramsUnit: 50 },
      ]);
    });

    it('rejects 0', () => {
      expect(Weight.validateWeightInKilograms(0)).toEqual([{ kind: 'positive' }]);
    });

    it('invalidates a non positive weight not in increment', () => {
      expect(Weight.validateWeightInKilograms(-12.34)).toEqual([
        { kind: 'positive' },
        { kind: 'multiple-of-grams-unit', gramsUnit: 50 },
      ]);
    });
  });
});
