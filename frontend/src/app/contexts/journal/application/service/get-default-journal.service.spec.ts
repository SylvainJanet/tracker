import { describe, expect, it, vi } from 'vitest';

import { calendarDate } from '../../domain/calendar-date';
import { type TodayProvider } from '../port/out/today.provider';
import { GetDefaultJournalDateService } from './get-default-journal-date.service';

describe('GetDefaultJournalDateService', () => {
  it('returns the date supplied by the today provider', () => {
    const date = calendarDate('2026-08-27');
    const todayOutcomeData = { today: date };
    const todayOutcome = { kind: 'success' as const, outcomeData: todayOutcomeData };
    const todayResult = { kind: 'success' as const, resultData: { defaultJournalDate: date } };

    const today = vi.fn<TodayProvider['today']>(() => todayOutcome);
    const provider: TodayProvider = { today };

    const service = new GetDefaultJournalDateService(provider);

    expect(service.get()).toEqual(todayResult);
    expect(today).toHaveBeenCalledOnce();
  });
});
