declare const calendarDateBrand: unique symbol;

export type CalendarDate = string & {
  readonly [calendarDateBrand]: true;
};

export function calendarDate(value: string): CalendarDate {
  if (!isCalendarDate(value)) {
    throw new RangeError(`"${value}" is not a valid ISO calendar date`);
  }

  return value;
}

export function isCalendarDate(value: string): value is CalendarDate {
  const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(value);

  if (match === null) {
    return false;
  }

  const year = Number(match[1]);
  const month = Number(match[2]);
  const day = Number(match[3]);

  const candidate = new Date(0);

  candidate.setUTCHours(0, 0, 0, 0);
  candidate.setUTCFullYear(year, month - 1, day);

  return (
    candidate.getUTCFullYear() === year &&
    candidate.getUTCMonth() === month - 1 &&
    candidate.getUTCDate() === day
  );
}

export function parseCalendarDate(value: string): CalendarDate | null {
  if (!isCalendarDate(value)) {
    return null;
  }

  return value as CalendarDate;
}
