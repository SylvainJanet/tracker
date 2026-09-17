import { type DailyRecord } from '../../../domain/daily-record';

export type CreateDailyRecordOutcome =
  | {
      readonly kind: 'created';
      readonly outcomeData: DailyRecord;
    }
  | {
      readonly kind: 'already-exists';
    };

export interface DailyRecordGateway {
  create(instruction: DailyRecord): Promise<CreateDailyRecordOutcome>;
}
