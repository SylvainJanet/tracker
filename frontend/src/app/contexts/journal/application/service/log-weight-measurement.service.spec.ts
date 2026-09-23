import { describe, expect, it, vi } from 'vitest';

import type {
  LogWeightMeasurementOutcomeData,
  WeightMeasurementStore,
} from '../port/out/weight-measurement.store';
import { LogWeightMeasurementService } from './log-weight-measurement.service';
import type { LogWeightMeasurementCommand } from '../port/in/log-weight-measurement.use-case';

describe('LogWeightMeasurementService', () => {
  it('returns the created weight measurement', async () => {
    const date = '2026-08-27';
    const weight = 70;
    const command: LogWeightMeasurementCommand = { date, weightInKg: weight };

    const outcomeData: LogWeightMeasurementOutcomeData = { weightInKg: weight, date: date };
    const log = vi.fn<WeightMeasurementStore['log']>(async () => ({
      kind: 'logged' as const,
      outcomeData,
    }));
    const store: WeightMeasurementStore = {
      log,
      getByDate: vi.fn(),
    };

    const service = new LogWeightMeasurementService(store);

    await expect(service.log(command)).resolves.toEqual({
      kind: 'logged',
      resultData: outcomeData,
    });
    expect(log).toHaveBeenCalledOnce();
    expect(log).toHaveBeenCalledWith(command);
  });
  it('rejects when the store reports a failure', async () => {
    const log = vi.fn<WeightMeasurementStore['log']>(async () => ({
      kind: 'failed' as const,
      outcomeData: {
        errorMessage: 'Database unavailable',
      },
    }));
    const store: WeightMeasurementStore = {
      log,
      getByDate: vi.fn(),
    };
    const service = new LogWeightMeasurementService(store);

    await expect(
      service.log({
        date: '2026-08-27',
        weightInKg: 70,
      }),
    ).rejects.toThrow('Failed to log weight measurement: Database unavailable');
  });

  it('rejects an invalid date returned by the store', async () => {
    const log = vi.fn<WeightMeasurementStore['log']>(async () => ({
      kind: 'logged' as const,
      outcomeData: {
        date: '2026-02-31',
        weightInKg: 70,
      },
    }));
    const store: WeightMeasurementStore = {
      log,
      getByDate: vi.fn(),
    };
    const service = new LogWeightMeasurementService(store);

    await expect(
      service.log({
        date: '2026-08-27',
        weightInKg: 70,
      }),
    ).rejects.toThrow('"2026-02-31" is not a valid ISO calendar date');
  });

  it('rejects an invalid weight returned by the store', async () => {
    const log = vi.fn<WeightMeasurementStore['log']>(async () => ({
      kind: 'logged' as const,
      outcomeData: {
        date: '2026-08-27',
        weightInKg: 70.01,
      },
    }));
    const store: WeightMeasurementStore = {
      log,
      getByDate: vi.fn(),
    };
    const service = new LogWeightMeasurementService(store);

    await expect(
      service.log({
        date: '2026-08-27',
        weightInKg: 70,
      }),
    ).rejects.toThrow(
      '"70010" is not a valid weight in grams: must be a positive integer multiple of 50',
    );
  });

  it('does not store invalid weight measurement', async () => {
    const date = '2026-08-27';
    const weight = -10;
    const command: LogWeightMeasurementCommand = { date, weightInKg: weight };

    const log = vi.fn<WeightMeasurementStore['log']>();
    const store: WeightMeasurementStore = {
      log,
      getByDate: vi.fn(),
    };

    const service = new LogWeightMeasurementService(store);

    await expect(() => service.log(command)).rejects.toThrow();

    expect(log).not.toHaveBeenCalled();
  });

  it('rejects undefined command', async () => {
    const log = vi.fn<WeightMeasurementStore['log']>();
    const store: WeightMeasurementStore = {
      log,
      getByDate: vi.fn(),
    };

    const service = new LogWeightMeasurementService(store);

    await expect(() => service.log(undefined as never)).rejects.toThrow('command must not be null');

    expect(log).not.toHaveBeenCalled();
  });
});
