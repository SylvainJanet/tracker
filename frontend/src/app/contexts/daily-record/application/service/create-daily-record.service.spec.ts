import { describe, expect, it, vi } from 'vitest';

import { calendarDate } from '../../domain/calendar-date';
import { type DailyRecord } from '../../domain/daily-record';
import { type DailyRecordGateway } from '../port/out/daily-record.gateway';
import { CreateDailyRecordService } from './create-daily-record.service';

describe('CreateDailyRecordService', () => {
  const command = calendarDate('2026-08-27');

  it('returns the created daily record', async () => {
    const outcomeData: DailyRecord = {
      date: command,
      status: 'IN_PROGRESS',
    };

    const create = vi.fn<DailyRecordGateway['create']>(async () => ({
      kind: 'created',
      outcomeData,
    }));
    const gateway: DailyRecordGateway = {
      findByDate: async () => {
        throw new Error('not used by this test');
      },
      create,
    };

    const service = new CreateDailyRecordService(gateway);

    await expect(service.execute(command)).resolves.toEqual({
      kind: 'created',
      resultData: outcomeData,
    });
    expect(create).toHaveBeenCalledOnce();
    expect(create).toHaveBeenCalledWith(command);
  });

  it('returns already-exists when creation conflicts', async () => {
    const create = vi.fn<DailyRecordGateway['create']>(async () => ({
      kind: 'already-exists',
    }));
    const gateway: DailyRecordGateway = {
      findByDate: async () => {
        throw new Error('not used by this test');
      },
      create,
    };

    const service = new CreateDailyRecordService(gateway);

    await expect(service.execute(command)).resolves.toEqual({
      kind: 'already-exists',
    });
    expect(create).toHaveBeenCalledOnce();
    expect(create).toHaveBeenCalledWith(command);
  });
});
