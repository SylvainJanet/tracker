import { describe, expect, it } from 'vitest';

import { calendarDate, isCalendarDate, parseCalendarDate } from './calendar-date';

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

  describe('isCalendarDate function', () => {
    it('accepts a leap day in a leap year', () => {
      expect(isCalendarDate('2024-02-29')).toBe(true);
    });

    it('rejects an impossible calendar date', () => {
      expect(isCalendarDate('2025-02-29')).toBe(false);
    });

    it('rejects a non-ISO representation', () => {
      expect(isCalendarDate('27/08/2026')).toBe(false);
      expect(isCalendarDate('2026-8-27')).toBe(false);
    });
  });

  describe('parseCalendarDate function', () => {
    it('returns a CalendarDate for a valid ISO calendar date', () => {
      expect(parseCalendarDate('2026-08-27')).toBe('2026-08-27');
    });

    it('returns null for an invalid ISO calendar date', () => {
      expect(parseCalendarDate('2026-04-31')).toBeNull();
    });
  });
});
