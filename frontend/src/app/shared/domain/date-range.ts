import type { CalendarDate } from './calendar-date';

export class DateRange {
  constructor(
    readonly startDate: CalendarDate,
    readonly endDate: CalendarDate,
  ) {
    if (!startDate) {
      throw new TypeError('start date must not be null');
    }

    if (!endDate) {
      throw new TypeError('end date must not be null');
    }

    if (startDate > endDate) {
      throw new RangeError('start date must not be after end date');
    }
  }

  contains(date: CalendarDate): boolean {
    return date >= this.startDate && date <= this.endDate;
  }
}
