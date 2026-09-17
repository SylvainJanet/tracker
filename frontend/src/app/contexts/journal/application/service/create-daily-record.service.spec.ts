import { describe, expect, it, vi } from 'vitest';

import { dailyRecord, type DailyRecord } from '../../domain/daily-record';
import { type DailyRecordGateway } from '../port/out/daily-record.gateway';
import { CreateDailyRecordService } from './create-daily-record.service';

describe('CreateDailyRecordService', () => {
  const command = dailyRecord('2026-08-27', 70);

  it('returns the created daily record', async () => {
    const outcomeData: DailyRecord = command;

    const create = vi.fn<DailyRecordGateway['create']>(async () => ({
      kind: 'created',
      outcomeData,
    }));
    const gateway: DailyRecordGateway = {
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
