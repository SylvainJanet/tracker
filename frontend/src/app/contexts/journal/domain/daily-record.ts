import { calendarDate, type CalendarDate } from './calendar-date';
import { weight, type Weight } from './weight';

const dailyRecordBrand: unique symbol = Symbol('weightBrand');

export interface DailyRecord {
  readonly date: CalendarDate;
  readonly weight: Weight;
  readonly [dailyRecordBrand]: true;
}

export function dailyRecord(rawDate: string, rawWeight: number): DailyRecord {
  return Object.freeze({
    date: calendarDate(rawDate),
    weight: weight(rawWeight),
    [dailyRecordBrand]: true as const,
  });
}
