import { beforeEach, describe, expect, it, vi } from 'vitest';
import type { WeightMeasurementStore } from '../port/out/weight-measurement.store';
import { GetWeightMeasurementByDateService } from './get-weight-measurement-by-date.service';
import { calendarDate } from '../../domain/calendar-date';
import type { GetWeightMeasurementByDateQuery } from '../port/in/get-weight-measurement-by-date.use-case';

describe('GetWeightMeasurementByDateService', () => {
  let store: WeightMeasurementStore;
  let service: GetWeightMeasurementByDateService;

  const date = '2026-09-16';

  beforeEach(() => {
    store = {
      getByDate: vi.fn(),
    } as unknown as WeightMeasurementStore;

    service = new GetWeightMeasurementByDateService(store);
  });

  it('returns the measurement when found', async () => {
    vi.mocked(store.getByDate).mockResolvedValue({
      kind: 'found',
      outcomeData: {
        date: calendarDate(date),
        weightInKg: 75.6,
      },
    });

    const result = await service.get({ date });

    expect(result).toEqual({
      kind: 'found',
      resultData: {
        date: calendarDate(date),
        weightInKg: 75.6,
      },
    });

    expect(store.getByDate).toHaveBeenCalledExactlyOnceWith({
      date: calendarDate(date),
    });
  });

  it('returns not-found when no measurement exists', async () => {
    vi.mocked(store.getByDate).mockResolvedValue({
      kind: 'not-found',
    });

    const result = await service.get({ date });

    expect(result).toEqual({ kind: 'not-found' });
    expect(store.getByDate).toHaveBeenCalledExactlyOnceWith({
      date: calendarDate(date),
    });
  });

  it('rejects an invalid query date without calling the store', async () => {
    await expect(
      service.get({
        date: '2026-02-31',
      }),
    ).rejects.toThrow('"2026-02-31" is not a valid ISO calendar date');

    expect(store.getByDate).not.toHaveBeenCalled();
  });

  it('rejects an invalid date returned by the store', async () => {
    vi.mocked(store.getByDate).mockResolvedValue({
      kind: 'found',
      outcomeData: {
        date: '2026-02-31',
        weightInKg: 75.6,
      },
    });

    await expect(service.get({ date })).rejects.toThrow(
      '"2026-02-31" is not a valid ISO calendar date',
    );
  });

  it('rejects an invalid weight returned by the store', async () => {
    vi.mocked(store.getByDate).mockResolvedValue({
      kind: 'found',
      outcomeData: {
        date,
        weightInKg: 75.61,
      },
    });

    await expect(service.get({ date })).rejects.toThrow(
      '"75610" is not a valid weight in grams: must be a positive integer multiple of 50',
    );
  });

  it('rejects when the store reports a failure', async () => {
    vi.mocked(store.getByDate).mockResolvedValue({
      kind: 'failed',
      outcomeData: {
        errorMessage: 'Database unavailable',
      },
    });

    await expect(service.get({ date })).rejects.toThrow(
      'Failed to get weight measurement: Database unavailable',
    );
  });

  it('rejects a null query without calling the store', async () => {
    await expect(service.get(null as unknown as GetWeightMeasurementByDateQuery)).rejects.toThrow(
      'query must not be null',
    );

    expect(store.getByDate).not.toHaveBeenCalled();
  });

  it('propagates unexpected store exceptions', async () => {
    const error = new Error('Unexpected database error');

    vi.mocked(store.getByDate).mockRejectedValue(error);

    await expect(service.get({ date })).rejects.toBe(error);
  });
});
