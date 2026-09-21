import type { TodayOutcomeData, TodayProvider } from '../port/out/today.provider';
import type {
  GetDefaultJournalDateResult,
  GetDefaultJournalDateResultData,
  GetDefaultJournalDateUseCase,
} from '../port/in/get-default-journal-date.use-case';
import { calendarDate, type CalendarDate } from '../../domain/calendar-date';

export class GetDefaultJournalDateService implements GetDefaultJournalDateUseCase {
  constructor(private readonly provider: TodayProvider) {}
  get(): GetDefaultJournalDateResult {
    const outcome = this.provider.today();
    const outcomeData = outcome.outcomeData;

    const domain = toDomain(outcomeData);

    return {
      kind: 'success',
      resultData: toResultData(domain),
    };
  }
}

function toDomain(outcomeData: TodayOutcomeData): CalendarDate {
  return calendarDate(outcomeData.today);
}

function toResultData(domain: CalendarDate): GetDefaultJournalDateResultData {
  return {
    defaultJournalDate: domain,
  };
}
