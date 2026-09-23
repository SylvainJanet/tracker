import { firstValueFrom, lastValueFrom } from 'rxjs';
import { describe, expect, it, vi } from 'vitest';

import type { GetDefaultJournalDateUseCase } from '../../../../../application/port/in/get-default-journal-date.use-case';
import type {
  GetWeightMeasurementByDateResult,
  GetWeightMeasurementByDateUseCase,
} from '../../../../../application/port/in/get-weight-measurement-by-date.use-case';
import type {
  LogWeightMeasurementResult,
  LogWeightMeasurementUseCase,
} from '../../../../../application/port/in/log-weight-measurement.use-case';
import { Weight } from '../../../../../domain/weight';
import { JournalLoggingPresenter } from './journal.logging.presenter';

describe('JournalLoggingPresenter', () => {
  const defaultDate = '2026-09-20';
  const weightInKg = 72.5;

  const loggedResult: LogWeightMeasurementResult = {
    kind: 'logged',
    resultData: {
      date: defaultDate,
      weightInKg,
    },
  };

  const foundResult: GetWeightMeasurementByDateResult = {
    kind: 'found',
    resultData: {
      date: defaultDate,
      weightInKg,
    },
  };

  const notFoundResult: GetWeightMeasurementByDateResult = {
    kind: 'not-found',
  };

  it('starts with idle workflows, the default date and an empty weight form', () => {
    const presenter = createPresenter();

    expect(presenter.state()).toEqual({
      logState: { kind: 'idle' },
      getState: { kind: 'idle' },
    });
    expect(presenter.logLoading).toBe(false);
    expect(presenter.getLoading).toBe(false);
    expect(presenter.dateControl.value).toBe(defaultDate);
    expect(presenter.validDate()).toBe(true);
    expect(presenter.weightControl.value).toBeNull();
    expect(presenter.weightStepInKg).toBe(Weight.measurementIncrementInKilograms());
    expect(presenter.canEditWeightToLog()).toBe(false);
    expect(presenter.showWeightErrors()).toBe(false);
  });

  describe('validDate', () => {
    it('returns false for an invalid calendar date', () => {
      const presenter = createPresenter();

      presenter.dateControl.setValue('2026-02-31');

      expect(presenter.validDate()).toBe(false);
    });

    it('returns false for a missing date', () => {
      const presenter = createPresenter();

      presenter.dateControl.setValue('');

      expect(presenter.validDate()).toBe(false);
    });
  });

  describe('selectDate', () => {
    it('does not invoke the use case for an invalid date', async () => {
      const get = vi.fn<GetWeightMeasurementByDateUseCase['get']>(async () => foundResult);
      const presenter = createPresenter({ get });

      await complete(presenter.selectDate('2026-02-31'));

      expect(get).not.toHaveBeenCalled();
      expect(presenter.dateControl.value).toBe('2026-02-31');
      expect(presenter.dateControl.touched).toBe(true);
      expect(presenter.state()).toEqual({
        logState: { kind: 'idle' },
        getState: { kind: 'idle' },
      });
    });

    it('gets and presents an existing measurement', async () => {
      const get = vi.fn<GetWeightMeasurementByDateUseCase['get']>(async () => foundResult);
      const presenter = createPresenter({ get });

      await complete(presenter.selectDate(defaultDate));

      expect(get).toHaveBeenCalledOnce();
      expect(get).toHaveBeenCalledWith({
        date: defaultDate,
      });
      expect(presenter.state()).toEqual({
        logState: { kind: 'idle' },
        getState: {
          kind: 'found',
          view: {
            date: defaultDate,
            weightInKg,
          },
        },
      });
      expect(presenter.getLoading).toBe(false);
      expect(presenter.canEditWeightToLog()).toBe(false);
      expect(presenter.dateControl.enabled).toBe(true);
      expect(presenter.weightForm.enabled).toBe(true);
    });

    it('presents an available date when no measurement exists', async () => {
      const presenter = createPresenter();

      await complete(presenter.selectDate(defaultDate));

      expect(presenter.state()).toEqual(notFoundState());
      expect(presenter.canEditWeightToLog()).toBe(true);
    });

    it('disables the controls while getting the measurement', async () => {
      const pendingResult = deferred<GetWeightMeasurementByDateResult>();
      const get = vi.fn<GetWeightMeasurementByDateUseCase['get']>(() => pendingResult.promise);
      const presenter = createPresenter({ get });

      const completion = complete(presenter.selectDate(defaultDate));

      expect(presenter.state()).toEqual({
        logState: { kind: 'idle' },
        getState: { kind: 'loading' },
      });
      expect(presenter.getLoading).toBe(true);
      expect(presenter.logLoading).toBe(false);
      expect(presenter.dateControl.disabled).toBe(true);
      expect(presenter.weightForm.disabled).toBe(true);
      expect(presenter.canEditWeightToLog()).toBe(false);

      pendingResult.resolve(notFoundResult);
      await completion;

      expect(presenter.getLoading).toBe(false);
      expect(presenter.dateControl.enabled).toBe(true);
      expect(presenter.weightForm.enabled).toBe(true);
    });

    it('presents a lookup failure and re-enables the controls', async () => {
      const get = vi.fn<GetWeightMeasurementByDateUseCase['get']>(async () => {
        throw new Error('backend unavailable');
      });
      const presenter = createPresenter({ get });

      await complete(presenter.selectDate(defaultDate));

      expect(presenter.state()).toEqual({
        logState: { kind: 'idle' },
        getState: {
          kind: 'failure',
          title: 'Unable to get weight measurement',
          message: 'The weight measurement could not be retrieved: backend unavailable',
        },
      });
      expect(presenter.getLoading).toBe(false);
      expect(presenter.canEditWeightToLog()).toBe(false);
      expect(presenter.dateControl.enabled).toBe(true);
      expect(presenter.weightForm.enabled).toBe(true);
    });

    it('retries a lookup after a failure', async () => {
      const get = vi
        .fn<GetWeightMeasurementByDateUseCase['get']>()
        .mockRejectedValueOnce(new Error('backend unavailable'))
        .mockResolvedValueOnce(notFoundResult);
      const presenter = createPresenter({ get });

      await complete(presenter.selectDate(defaultDate));
      await complete(presenter.selectDate(defaultDate));

      expect(get).toHaveBeenCalledTimes(2);
      expect(presenter.state()).toEqual(notFoundState());
      expect(presenter.canEditWeightToLog()).toBe(true);
    });
  });

  describe('canEditWeightToLog', () => {
    it('allows opening the form for an available date without requiring a weight yet', async () => {
      const presenter = createPresenter();

      await prepareAvailableDate(presenter);

      expect(presenter.weightControl.value).toBeNull();
      expect(presenter.weightControl.invalid).toBe(true);
      expect(presenter.canEditWeightToLog()).toBe(true);
    });

    it('does not allow opening the form when the selected date becomes invalid', async () => {
      const presenter = createPresenter();

      await prepareAvailableDate(presenter);
      presenter.dateControl.setValue('not-a-date');

      expect(presenter.canEditWeightToLog()).toBe(false);
    });
  });

  describe('log', () => {
    it('does not invoke the use case when the weight form is invalid', async () => {
      const log = vi.fn<LogWeightMeasurementUseCase['log']>(async () => loggedResult);
      const presenter = createPresenter({ log });
      await prepareAvailableDate(presenter);

      await expect(firstValueFrom(presenter.log())).resolves.toBe(false);

      expect(log).not.toHaveBeenCalled();
      expect(presenter.state()).toEqual(notFoundState());
      expect(presenter.weightControl.touched).toBe(true);
      expect(presenter.dateControl.touched).toBe(true);
      expect(presenter.showWeightErrors()).toBe(true);
    });

    it('does not invoke the use case when the date is invalid', async () => {
      const log = vi.fn<LogWeightMeasurementUseCase['log']>(async () => loggedResult);
      const presenter = createPresenter({ log });
      await prepareAvailableDate(presenter);

      enterValidWeight(presenter);
      presenter.dateControl.setValue('not-a-date');

      await expect(firstValueFrom(presenter.log())).resolves.toBe(false);

      expect(log).not.toHaveBeenCalled();
      expect(presenter.state()).toEqual(notFoundState());
    });

    it('logs and presents the successful result', async () => {
      const log = vi.fn<LogWeightMeasurementUseCase['log']>(async () => loggedResult);
      const presenter = createPresenter({ log });
      await prepareAvailableDate(presenter);

      enterValidWeight(presenter);

      await expect(firstValueFrom(presenter.log())).resolves.toBe(true);

      expect(log).toHaveBeenCalledOnce();
      expect(log).toHaveBeenCalledWith({
        date: defaultDate,
        weightInKg,
      });
      expect(presenter.state()).toEqual({
        logState: {
          kind: 'logged',
          view: {
            date: defaultDate,
            weightInKg,
          },
        },
        getState: { kind: 'idle' },
      });
      expect(presenter.logLoading).toBe(false);
      expect(presenter.dateControl.enabled).toBe(true);
      expect(presenter.weightForm.enabled).toBe(true);
    });

    it('disables the controls and ignores another request while logging', async () => {
      const pendingResult = deferred<LogWeightMeasurementResult>();
      const log = vi.fn<LogWeightMeasurementUseCase['log']>(() => pendingResult.promise);
      const presenter = createPresenter({ log });
      await prepareAvailableDate(presenter);

      enterValidWeight(presenter);

      const completion = firstValueFrom(presenter.log());

      expect(presenter.state()).toEqual({
        logState: { kind: 'loading' },
        getState: notFoundState().getState,
      });
      expect(presenter.logLoading).toBe(true);
      expect(presenter.getLoading).toBe(false);
      expect(presenter.dateControl.disabled).toBe(true);
      expect(presenter.weightForm.disabled).toBe(true);
      expect(presenter.canEditWeightToLog()).toBe(false);

      await expect(firstValueFrom(presenter.log())).resolves.toBe(false);
      expect(log).toHaveBeenCalledOnce();

      pendingResult.resolve(loggedResult);
      await expect(completion).resolves.toBe(true);

      expect(presenter.dateControl.enabled).toBe(true);
      expect(presenter.weightForm.enabled).toBe(true);
    });

    it('presents a logging failure and permits a retry', async () => {
      const log = vi.fn<LogWeightMeasurementUseCase['log']>(async () => {
        throw new Error('backend unavailable');
      });
      const presenter = createPresenter({ log });
      await prepareAvailableDate(presenter);

      enterValidWeight(presenter);

      await expect(firstValueFrom(presenter.log())).resolves.toBe(false);

      expect(presenter.state()).toEqual({
        logState: {
          kind: 'failure',
          title: 'Unable to log weight measurement',
          message: 'The weight measurement could not be logged: backend unavailable',
        },
        getState: notFoundState().getState,
      });
      expect(presenter.logLoading).toBe(false);
      expect(presenter.dateControl.enabled).toBe(true);
      expect(presenter.weightForm.enabled).toBe(true);
      expect(presenter.canEditWeightToLog()).toBe(true);
    });

    it('retries logging after a failure', async () => {
      const log = vi
        .fn<LogWeightMeasurementUseCase['log']>()
        .mockRejectedValueOnce(new Error('backend unavailable'))
        .mockResolvedValueOnce(loggedResult);
      const presenter = createPresenter({ log });
      await prepareAvailableDate(presenter);

      enterValidWeight(presenter);

      await expect(firstValueFrom(presenter.log())).resolves.toBe(false);
      await expect(firstValueFrom(presenter.log())).resolves.toBe(true);

      expect(log).toHaveBeenCalledTimes(2);
      expect(presenter.state()).toEqual({
        logState: {
          kind: 'logged',
          view: {
            date: defaultDate,
            weightInKg,
          },
        },
        getState: { kind: 'idle' },
      });
    });
  });

  describe('showWeightErrors', () => {
    it('shows errors after the weight control becomes dirty', () => {
      const presenter = createPresenter();

      presenter.weightControl.markAsDirty();

      expect(presenter.showWeightErrors()).toBe(true);
    });

    it('shows errors after an attempted submission', async () => {
      const presenter = createPresenter();

      await firstValueFrom(presenter.log());

      expect(presenter.showWeightErrors()).toBe(true);
    });
  });

  describe('resetForm', () => {
    it('clears the weight and its presentation state', async () => {
      const presenter = createPresenter();

      await firstValueFrom(presenter.log());
      expect(presenter.showWeightErrors()).toBe(true);

      presenter.resetForm();

      expect(presenter.weightControl.value).toBeNull();
      expect(presenter.weightControl.pristine).toBe(true);
      expect(presenter.weightControl.untouched).toBe(true);
      expect(presenter.showWeightErrors()).toBe(false);
    });
  });

  describe('cancelLog', () => {
    it('clears the form and cancels a failed logging attempt', async () => {
      const presenter = createPresenter({
        log: async () => {
          throw new Error('backend unavailable');
        },
      });
      await prepareAvailableDate(presenter);

      enterValidWeight(presenter);
      await firstValueFrom(presenter.log());

      presenter.cancelLog();

      expect(presenter.state()).toEqual(notFoundState());
      expect(presenter.weightControl.value).toBeNull();
      expect(presenter.showWeightErrors()).toBe(false);
    });

    it('clears the form without changing an available-date workflow', async () => {
      const presenter = createPresenter();
      await prepareAvailableDate(presenter);
      enterValidWeight(presenter);

      presenter.cancelLog();

      expect(presenter.state()).toEqual(notFoundState());
      expect(presenter.weightControl.value).toBeNull();
      expect(presenter.showWeightErrors()).toBe(false);
    });
  });

  function createPresenter(
    dependencies: {
      readonly log?: LogWeightMeasurementUseCase['log'];
      readonly get?: GetWeightMeasurementByDateUseCase['get'];
    } = {},
  ): JournalLoggingPresenter {
    const logWeightMeasurementUseCase: LogWeightMeasurementUseCase = {
      log: dependencies.log ?? (async () => loggedResult),
    };

    const getWeightMeasurementByDateUseCase: GetWeightMeasurementByDateUseCase = {
      get: dependencies.get ?? (async () => notFoundResult),
    };

    const getDefaultJournalDateUseCase: GetDefaultJournalDateUseCase = {
      get: () => ({
        kind: 'success',
        resultData: {
          defaultJournalDate: defaultDate,
        },
      }),
    };

    return new JournalLoggingPresenter(
      logWeightMeasurementUseCase,
      getWeightMeasurementByDateUseCase,
      getDefaultJournalDateUseCase,
    );
  }

  async function prepareAvailableDate(presenter: JournalLoggingPresenter): Promise<void> {
    await complete(presenter.selectDate(defaultDate));
  }

  function enterValidWeight(presenter: JournalLoggingPresenter): void {
    presenter.weightControl.setValue(weightInKg);
  }

  function notFoundState() {
    return {
      logState: { kind: 'idle' as const },
      getState: {
        kind: 'not-found' as const,
        title: 'No weight measurement found',
        message: 'No weight measurement has been logged at this date.',
      },
    };
  }
});

async function complete(
  observable: ReturnType<JournalLoggingPresenter['selectDate']>,
): Promise<void> {
  await lastValueFrom(observable, {
    defaultValue: undefined,
  });
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
