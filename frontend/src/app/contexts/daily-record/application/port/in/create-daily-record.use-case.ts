import { type CalendarDate } from '../../../domain/calendar-date';
import { type DailyRecord } from '../../../domain/daily-record';

export type CreateDailyRecordResult =
  | {
      readonly kind: 'created';
      readonly resultData: DailyRecord;
    }
  | {
      readonly kind: 'already-exists';
    };

export interface CreateDailyRecordUseCase {
  execute(command: CalendarDate): Promise<CreateDailyRecordResult>;
}
