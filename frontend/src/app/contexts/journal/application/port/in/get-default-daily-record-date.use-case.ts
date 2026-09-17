import type { CalendarDate } from '../../../domain/calendar-date';

export interface GetDefaultDailyRecordDateResult {
  readonly resultData: CalendarDate;
}

export interface GetDefaultDailyRecordDateUseCase {
  execute(): GetDefaultDailyRecordDateResult;
}
