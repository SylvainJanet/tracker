import type {
  GetDefaultDailyRecordDateResult,
  GetDefaultDailyRecordDateUseCase,
} from '../port/in/get-default-daily-record-date.use-case';
import type { TodayProvider } from '../port/out/today.provider';

export class GetDefaultDailyRecordDateService implements GetDefaultDailyRecordDateUseCase {
  constructor(private readonly provider: TodayProvider) {}
  execute(): GetDefaultDailyRecordDateResult {
    return {
      resultData: this.provider.today(),
    };
  }
}
