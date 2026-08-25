import {
  type GetDailyRecordResult,
  type GetDailyRecordUseCase,
} from '../port/in/get-daily-record.use-case';
import { type DailyRecordGateway } from '../port/out/daily-record.gateway';
import { type CalendarDate } from '../../domain/calendar-date';

export class GetDailyRecordService implements GetDailyRecordUseCase {
  constructor(private readonly gateway: DailyRecordGateway) {}

  async execute(query: CalendarDate): Promise<GetDailyRecordResult> {
    const outcome = await this.gateway.findByDate(query);

    if (outcome.kind === 'not-found') {
      return {
        kind: 'not-found',
      };
    }

    return {
      kind: 'found',
      resultData: outcome.outcomeData,
    };
  }
}
