import { describe, expect, it, vi } from 'vitest';

import { calendarDate } from '../../domain/calendar-date';
import { type DailyRecord } from '../../domain/daily-record';
import { type DailyRecordGateway } from '../port/out/daily-record.gateway';
import { GetDailyRecordService } from './get-daily-record.service';

describe('GetDailyRecordService', () => {
  const query = calendarDate('2026-08-27');

  it('returns an existing daily record', async () => {
    const outcomeData: DailyRecord = {
      date: query,
      status: 'IN_PROGRESS',
    };

    const findByDate = vi.fn<DailyRecordGateway['findByDate']>(async () => ({
      kind: 'found',
      outcomeData,
    }));
    const gateway: DailyRecordGateway = {
      findByDate,
      create: async () => {
        throw new Error('not used by this test');
      },
    };

    const service = new GetDailyRecordService(gateway);

    await expect(service.execute(query)).resolves.toEqual({
      kind: 'found',
      resultData: outcomeData,
    });
    expect(findByDate).toHaveBeenCalledOnce();
    expect(findByDate).toHaveBeenCalledWith(query);
  });

  it('returns not-found when no record exists', async () => {
    const findByDate = vi.fn<DailyRecordGateway['findByDate']>(async () => ({
      kind: 'not-found',
    }));
    const gateway: DailyRecordGateway = {
      findByDate,
      create: async () => {
        throw new Error('not used by this test');
      },
    };

    const service = new GetDailyRecordService(gateway);

    await expect(service.execute(query)).resolves.toEqual({
      kind: 'not-found',
    });
    expect(findByDate).toHaveBeenCalledOnce();
    expect(findByDate).toHaveBeenCalledWith(query);
  });
});
