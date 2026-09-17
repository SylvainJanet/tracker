import {
  type CreateDailyRecordResult,
  type CreateDailyRecordUseCase,
} from '../port/in/create-daily-record.use-case';
import { type DailyRecordGateway } from '../port/out/daily-record.gateway';
import type { DailyRecord } from '../../domain/daily-record';

export class CreateDailyRecordService implements CreateDailyRecordUseCase {
  constructor(private readonly gateway: DailyRecordGateway) {}

  async execute(command: DailyRecord): Promise<CreateDailyRecordResult> {
    const outcome = await this.gateway.create(command);

    if (outcome.kind === 'already-exists') {
      return {
        kind: 'already-exists',
      };
    }

    return {
      kind: 'created',
      resultData: outcome.outcomeData,
    };
  }
}
