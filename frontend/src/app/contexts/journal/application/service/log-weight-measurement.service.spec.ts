import { describe, expect, it, vi } from 'vitest';

import type {
  LogWeightMeasurementOutcomeData,
  LogWeightMeasurementStore,
} from '../port/out/log-weight-measurement.store';
import { LogWeightMeasurementService } from './log-weight-measurement.service';
import type { LogWeightMeasurementCommand } from '../port/in/log-weight-measurement.use-case';

describe('LogWeightMeasurementService', () => {
  it('returns the created weight measurement', async () => {
    const date = '2026-08-27';
    const weight = 70;
    const command: LogWeightMeasurementCommand = { date, weightInKg: weight };

    const outcomeData: LogWeightMeasurementOutcomeData = { weightInKg: weight, date: date };
    const log = vi.fn<LogWeightMeasurementStore['log']>(async () => ({
      kind: 'logged' as const,
      outcomeData,
    }));
    const store: LogWeightMeasurementStore = {
      log,
    };

    const service = new LogWeightMeasurementService(store);

    await expect(service.log(command)).resolves.toEqual({
      kind: 'logged',
      resultData: outcomeData,
    });
    expect(log).toHaveBeenCalledOnce();
    expect(log).toHaveBeenCalledWith(command);
  });

  it('does not store invalid weight measurement', async () => {
    const date = '2026-08-27';
    const weight = -10;
    const command: LogWeightMeasurementCommand = { date, weightInKg: weight };

    const log = vi.fn<LogWeightMeasurementStore['log']>();
    const store: LogWeightMeasurementStore = {
      log,
    };

    const service = new LogWeightMeasurementService(store);

    await expect(() => service.log(command)).rejects.toThrow();

    expect(log).not.toHaveBeenCalled();
  });

  it('rejects undefined command', async () => {
    const log = vi.fn<LogWeightMeasurementStore['log']>();
    const store: LogWeightMeasurementStore = {
      log,
    };

    const service = new LogWeightMeasurementService(store);

    await expect(() => service.log(undefined as never)).rejects.toThrow('command must not be null');

    expect(log).not.toHaveBeenCalled();
  });
});
