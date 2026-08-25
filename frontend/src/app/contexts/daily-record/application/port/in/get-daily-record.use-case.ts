import { type CalendarDate } from '../../../domain/calendar-date';
import { type DailyRecord } from '../../../domain/daily-record';

export type GetDailyRecordResult =
  | {
      readonly kind: 'found';
      readonly resultData: DailyRecord;
    }
  | {
      readonly kind: 'not-found';
    };

export interface GetDailyRecordUseCase {
  execute(query: CalendarDate): Promise<GetDailyRecordResult>;
}
