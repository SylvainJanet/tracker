import { describe, expect, it } from 'vitest';

import { Weight } from './weight';
import { WeightMeasurement } from './weight-measurement';
import { calendarDate } from './calendar-date';

describe('WeightMeasurement', () => {
  describe('WeightMeasurement.create function', () => {
    it('creates a valid weight measurement', () => {
      const date = calendarDate('2026-08-27');
      const weight = Weight.of(70);
      const weightMeasurement = WeightMeasurement.create(date, weight);

      expect(weightMeasurement.calendarDate).toEqual(date);
      expect(weightMeasurement.weight).toEqual(weight);
    });

    it('throws when the date is missing', () => {
      expect(() => WeightMeasurement.create(undefined as never, Weight.of(70))).toThrow(
        'calendarDate is required',
      );
    });

    it('throws when the weight is missing', () => {
      expect(() =>
        WeightMeasurement.create(calendarDate('2026-08-27'), undefined as never),
      ).toThrow('weight is required');
    });
  });
});
