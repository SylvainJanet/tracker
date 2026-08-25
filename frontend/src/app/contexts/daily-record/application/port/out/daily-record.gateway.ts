import { type CalendarDate } from '../../../domain/calendar-date';
import { type DailyRecord } from '../../../domain/daily-record';

export type CreateDailyRecordOutcome =
  | {
      readonly kind: 'created';
      readonly outcomeData: DailyRecord;
    }
  | {
      readonly kind: 'already-exists';
    };

export type FindDailyRecordOutcome =
  | {
      readonly kind: 'found';
      readonly outcomeData: DailyRecord;
    }
  | {
      readonly kind: 'not-found';
    };

export interface DailyRecordGateway {
  findByDate(criteria: CalendarDate): Promise<FindDailyRecordOutcome>;

  create(instruction: CalendarDate): Promise<CreateDailyRecordOutcome>;
}
