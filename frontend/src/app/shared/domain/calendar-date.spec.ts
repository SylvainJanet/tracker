import { describe, expect, it } from 'vitest';

import { calendarDate } from './calendar-date';

describe('CalendarDate', () => {
  describe('calendarDate function', () => {
    it('accepts a valid ISO calendar date', () => {
      expect(calendarDate('2026-08-27')).toBe('2026-08-27');
    });

    it('throws when constructing an invalid date', () => {
      expect(() => calendarDate('2026-04-31')).toThrow(
        '"2026-04-31" is not a valid ISO calendar date',
      );
    });
  });
});
