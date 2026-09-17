import { describe, expect, it, vi } from 'vitest';

import { calendarDate } from '../../domain/calendar-date';
import { type TodayProvider } from '../port/out/today.provider';
import { GetDefaultDailyRecordDateService } from './get-default-daily-record-date.service';

describe('GetDefaultDailyRecordDateService', () => {
  it('returns the date supplied by the today provider', () => {
    const date = calendarDate('2026-08-27');
    const today = vi.fn<TodayProvider['today']>(() => date);
    const provider: TodayProvider = { today };

    const service = new GetDefaultDailyRecordDateService(provider);

    expect(service.execute()).toEqual({ resultData: date });
    expect(today).toHaveBeenCalledOnce();
  });
});
