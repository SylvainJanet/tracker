import { describe, expect, it } from 'vitest';

import { calendarDate } from './calendar-date';
import { DateRange } from './date-range';

describe('DateRange', () => {
  it('creates an inclusive date range', () => {
    const startDate = calendarDate('2026-09-20');
    const endDate = calendarDate('2026-09-25');

    const range = DateRange.create(startDate, endDate);

    expect(range.startDate).toBe(startDate);
    expect(range.endDate).toBe(endDate);
    expect(range.contains(startDate)).toBe(true);
    expect(range.contains(endDate)).toBe(true);
  });

  it('accepts the same date as both boundaries', () => {
    const date = calendarDate('2026-09-20');

    expect(() => DateRange.create(date, date)).not.toThrow();
  });

  it('rejects a missing start date', () => {
    expect(() => DateRange.create(undefined as never, calendarDate('2026-09-25'))).toThrow(
      'start date must not be null',
    );
  });

  it('rejects a missing end date', () => {
    expect(() => DateRange.create(calendarDate('2026-09-20'), undefined as never)).toThrow(
      'end date must not be null',
    );
  });

  it('rejects a start date after the end date', () => {
    expect(() => DateRange.create(calendarDate('2026-09-25'), calendarDate('2026-09-20'))).toThrow(
      'start date must not be after end date',
    );
  });

  it('identifies a date outside the range', () => {
    const range = DateRange.create(calendarDate('2026-09-20'), calendarDate('2026-09-25'));

    expect(range.contains(calendarDate('2026-09-19'))).toBe(false);
    expect(range.contains(calendarDate('2026-09-26'))).toBe(false);
  });
});
