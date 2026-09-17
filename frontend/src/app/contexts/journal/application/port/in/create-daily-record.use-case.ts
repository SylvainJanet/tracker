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
  execute(command: DailyRecord): Promise<CreateDailyRecordResult>;
}
