import { describe, expect, it } from 'vitest';

import { weight, isWeight, parseWeight } from './weight';

describe('Weight', () => {
  describe('weight function', () => {
    it('accepts a valid weight', () => {
      expect(weight(70)).toBe(70);
    });

    it('throws when constructing an invalid weight', () => {
      expect(() => weight(-5)).toThrow('"-5" is not a valid weight: must be a positive number');
    });
  });

  describe('isWeight function', () => {
    it('accepts a valid weight', () => {
      expect(isWeight(70)).toBe(true);
    });

    it('rejects an invalid weight', () => {
      expect(isWeight(-5)).toBe(false);
    });

    it('accepts 0 as a valid weight', () => {
      expect(isWeight(0)).toBe(true);
    });
  });

  describe('parseWeight function', () => {
    it('returns a Weight for a valid weight', () => {
      expect(parseWeight(70)).toBe(70);
    });

    it('returns null for an invalid weight', () => {
      expect(parseWeight(-5)).toBeNull();
    });
  });
});
