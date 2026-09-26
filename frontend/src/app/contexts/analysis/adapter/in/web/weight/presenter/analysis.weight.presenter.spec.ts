import { lastValueFrom } from 'rxjs';
import { describe, expect, it, vi } from 'vitest';

import type {
  GetWeightAnalysisResult,
  GetWeightAnalysisUseCase,
  WeightAnalysisResultData,
} from '../../../../../application/port/in/get-weight-analysis.use-case';
import { AnalysisWeightPresenter } from './analysis.weight.presenter';

describe('AnalysisWeightPresenter', () => {
  const resultData: WeightAnalysisResultData = {
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

  const dataResult: GetWeightAnalysisResult = {
    kind: 'data',
    resultData,
  };

  const emptyResult: GetWeightAnalysisResult = {
    kind: 'empty',
  };

  it('automatically starts the analysis', async () => {
    const pendingResult = deferred<GetWeightAnalysisResult>();
    const get = vi.fn<GetWeightAnalysisUseCase['get']>(() => pendingResult.promise);

    const presenter = new AnalysisWeightPresenter({ get });

    expect(get).toHaveBeenCalledOnce();
    expect(presenter.state()).toEqual({
      analysisState: { kind: 'loading' },
    });
    expect(presenter.loading).toBe(true);

    pendingResult.resolve(emptyResult);
    await settle();
  });

  it('presents weight analysis data', async () => {
    const get = vi.fn<GetWeightAnalysisUseCase['get']>(async () => dataResult);

    const presenter = new AnalysisWeightPresenter({ get });
    await settle();

    expect(presenter.state()).toEqual({
      analysisState: {
        kind: 'analyzed',
        view: {
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
      },
    });
    expect(presenter.loading).toBe(false);
  });

  it('presents an empty analysis', async () => {
    const get = vi.fn<GetWeightAnalysisUseCase['get']>(async () => emptyResult);

    const presenter = new AnalysisWeightPresenter({ get });
    await settle();

    expect(presenter.state()).toEqual({
      analysisState: {
        kind: 'empty',
        title: 'No weight measurement found',
        message: 'Nothing to display: no weight measurement was found.',
      },
    });
    expect(presenter.loading).toBe(false);
  });

  it('presents an analysis failure', async () => {
    const get = vi.fn<GetWeightAnalysisUseCase['get']>(async () => {
      throw new Error('Analysis is unavailable');
    });

    const presenter = new AnalysisWeightPresenter({ get });
    await settle();

    expect(presenter.state()).toEqual({
      analysisState: {
        kind: 'failure',
        title: 'Unable to perform weight analysis',
        message: 'The weight analysis could not be performed: Analysis is unavailable',
      },
    });
    expect(presenter.loading).toBe(false);
  });

  it('ignores another analysis request while one is loading', async () => {
    const pendingResult = deferred<GetWeightAnalysisResult>();
    const get = vi.fn<GetWeightAnalysisUseCase['get']>(() => pendingResult.promise);
    const presenter = new AnalysisWeightPresenter({ get });

    await complete(presenter.analyze());

    expect(get).toHaveBeenCalledOnce();
    expect(presenter.state()).toEqual({
      analysisState: { kind: 'loading' },
    });

    pendingResult.resolve(dataResult);
    await settle();
  });

  it('retries the analysis after a failure', async () => {
    const pendingRetry = deferred<GetWeightAnalysisResult>();
    const get = vi
      .fn<GetWeightAnalysisUseCase['get']>()
      .mockRejectedValueOnce(new Error('Analysis is unavailable'))
      .mockReturnValueOnce(pendingRetry.promise);

    const presenter = new AnalysisWeightPresenter({ get });
    await settle();

    const retryCompletion = complete(presenter.analyze());

    expect(get).toHaveBeenCalledTimes(2);
    expect(presenter.state()).toEqual({
      analysisState: { kind: 'loading' },
    });

    pendingRetry.resolve(dataResult);
    await retryCompletion;

    expect(presenter.state()).toEqual({
      analysisState: {
        kind: 'analyzed',
        view: resultData,
      },
    });
    expect(presenter.loading).toBe(false);
  });
});

async function complete(observable: ReturnType<AnalysisWeightPresenter['analyze']>): Promise<void> {
  await lastValueFrom(observable, {
    defaultValue: undefined,
  });
}

async function settle(): Promise<void> {
  await Promise.resolve();
  await Promise.resolve();
}

function deferred<T>(): {
  readonly promise: Promise<T>;
  readonly resolve: (value: T) => void;
} {
  let resolve!: (value: T) => void;

  const promise = new Promise<T>((promiseResolve) => {
    resolve = promiseResolve;
  });

  return {
    promise,
    resolve,
  };
}
