import type {
  WeightAnalysisOutcomeData,
  WeightAnalysisStore,
} from '../port/out/weight-analysis.store';
import { GetWeightAnalysisService } from './get-weight-analysis.service';

describe('GetWeightAnalysisService', () => {
  const outcomeData: WeightAnalysisOutcomeData = {
    timelineStartDate: '2026-09-20',
    range: {
      startDate: '2026-09-20',
      endDate: '2026-09-25',
    },
    weightMeasurements: [
      {
        date: '2026-09-20',
        dayNumber: 1,
        weightInKg: 82.1,
      },
      {
        date: '2026-09-23',
        dayNumber: 4,
        weightInKg: 81.9,
      },
    ],
  };

  it('gets a populated weight analysis', async () => {
    const get = vi.fn<WeightAnalysisStore['get']>(async () => ({
      kind: 'data',
      outcomeData,
    }));
    const service = new GetWeightAnalysisService({ get });

    await expect(service.get()).resolves.toEqual({
      kind: 'data',
      resultData: {
        timelineStartDate: '2026-09-20',
        range: {
          startDate: '2026-09-20',
          endDate: '2026-09-25',
        },
        weightMeasurements: [
          {
            date: '2026-09-20',
            dayNumber: 1,
            weightInKg: 82.1,
          },
          {
            date: '2026-09-23',
            dayNumber: 4,
            weightInKg: 81.9,
          },
        ],
      },
    });

    expect(get).toHaveBeenCalledOnce();
  });

  it('gets a globally empty weight analysis', async () => {
    const get = vi.fn<WeightAnalysisStore['get']>(async () => ({
      kind: 'empty',
    }));
    const service = new GetWeightAnalysisService({ get });

    await expect(service.get()).resolves.toEqual({
      kind: 'empty',
    });
  });

  it('rejects store data that violates the analysis domain', async () => {
    const get = vi.fn<WeightAnalysisStore['get']>(async () => ({
      kind: 'data',
      outcomeData: {
        timelineStartDate: '2026-09-21',
        range: {
          startDate: '2026-09-20',
          endDate: '2026-09-25',
        },
        weightMeasurements: [],
      },
    }));
    const service = new GetWeightAnalysisService({ get });

    await expect(service.get()).rejects.toThrow(
      'timeline start date must not be after represented range start date',
    );
  });

  it('reports a failed retrieval', async () => {
    const get = vi.fn<WeightAnalysisStore['get']>(async () => ({
      kind: 'failed',
      outcomeData: {
        errorMessage: 'Analysis is unavailable',
      },
    }));
    const service = new GetWeightAnalysisService({ get });

    await expect(service.get()).rejects.toThrow(
      'Failed to get weight analysis: Analysis is unavailable',
    );
  });
});
