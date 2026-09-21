import { firstValueFrom } from 'rxjs';
import { describe, expect, it, vi } from 'vitest';

import type { GetDefaultJournalDateUseCase } from '../../../../../application/port/in/get-default-journal-date.use-case';
import type {
  LogWeightMeasurementResult,
  LogWeightMeasurementUseCase,
} from '../../../../../application/port/in/log-weight-measurement.use-case';
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

  it('starts idle with the default date and an empty weight form', () => {
    const presenter = createPresenter();

    expect(presenter.state()).toEqual({ kind: 'idle' });
    expect(presenter.loading).toBe(false);
    expect(presenter.dateControl.value).toBe(defaultDate);
    expect(presenter.validDate()).toBe(true);
    expect(presenter.weightControl.value).toBeNull();
    expect(presenter.canLog()).toBe(false);
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
    it('selects the date and clears the previous successful result', async () => {
      const presenter = createPresenter();

      enterValidWeight(presenter);
      await firstValueFrom(presenter.log());

      expect(presenter.state().kind).toBe('log-successful');

      presenter.selectDate('2026-09-21');

      expect(presenter.dateControl.value).toBe('2026-09-21');
      expect(presenter.state()).toEqual({ kind: 'idle' });
    });
  });

  describe('canLog', () => {
    it('returns true when the presenter is idle and both inputs are valid', () => {
      const presenter = createPresenter();

      enterValidWeight(presenter);

      expect(presenter.canLog()).toBe(true);
    });

    it('returns false when the weight is invalid', () => {
      const presenter = createPresenter();

      presenter.weightControl.setValue(-5);

      expect(presenter.canLog()).toBe(false);
    });

    it('returns false when the date is invalid', () => {
      const presenter = createPresenter();

      enterValidWeight(presenter);
      presenter.dateControl.setValue('not-a-date');

      expect(presenter.canLog()).toBe(false);
    });
  });

  describe('log', () => {
    it('does not invoke the use case when the weight form is invalid', async () => {
      const log = vi.fn<LogWeightMeasurementUseCase['log']>(async () => loggedResult);
      const presenter = createPresenter(log);

      await expect(firstValueFrom(presenter.log())).resolves.toBe(false);

      expect(log).not.toHaveBeenCalled();
      expect(presenter.state()).toEqual({ kind: 'idle' });
      expect(presenter.weightControl.touched).toBe(true);
      expect(presenter.dateControl.touched).toBe(true);
      expect(presenter.showWeightErrors()).toBe(true);
    });

    it('does not invoke the use case when the date is invalid', async () => {
      const log = vi.fn<LogWeightMeasurementUseCase['log']>(async () => loggedResult);
      const presenter = createPresenter(log);

      enterValidWeight(presenter);
      presenter.dateControl.setValue('not-a-date');

      await expect(firstValueFrom(presenter.log())).resolves.toBe(false);

      expect(log).not.toHaveBeenCalled();
      expect(presenter.state()).toEqual({ kind: 'idle' });
    });

    it('logs and presents the successful result', async () => {
      const log = vi.fn<LogWeightMeasurementUseCase['log']>(async () => loggedResult);
      const presenter = createPresenter(log);

      enterValidWeight(presenter);

      await expect(firstValueFrom(presenter.log())).resolves.toBe(true);

      expect(log).toHaveBeenCalledOnce();
      expect(log).toHaveBeenCalledWith({
        date: defaultDate,
        weightInKg,
      });
      expect(presenter.state()).toEqual({
        kind: 'log-successful',
        view: {
          date: defaultDate,
          weightInKg,
        },
      });
      expect(presenter.loading).toBe(false);
      expect(presenter.dateControl.enabled).toBe(true);
      expect(presenter.weightForm.enabled).toBe(true);
    });

    it('disables the controls and prevents another log while loading', async () => {
      const pendingResult = deferred<LogWeightMeasurementResult>();
      const log = vi.fn<LogWeightMeasurementUseCase['log']>(() => pendingResult.promise);
      const presenter = createPresenter(log);

      enterValidWeight(presenter);

      const completion = firstValueFrom(presenter.log());

      expect(presenter.state()).toEqual({ kind: 'loading' });
      expect(presenter.loading).toBe(true);
      expect(presenter.dateControl.disabled).toBe(true);
      expect(presenter.weightForm.disabled).toBe(true);
      expect(presenter.canLog()).toBe(false);

      await expect(firstValueFrom(presenter.log())).resolves.toBe(false);
      expect(log).toHaveBeenCalledOnce();

      pendingResult.resolve(loggedResult);

      await expect(completion).resolves.toBe(true);
      expect(presenter.dateControl.enabled).toBe(true);
      expect(presenter.weightForm.enabled).toBe(true);
    });

    it('presents a use-case failure and re-enables the controls', async () => {
      const log = vi.fn<LogWeightMeasurementUseCase['log']>(async () => {
        throw new Error('backend unavailable');
      });
      const presenter = createPresenter(log);

      enterValidWeight(presenter);

      await expect(firstValueFrom(presenter.log())).resolves.toBe(false);

      expect(presenter.state()).toEqual({
        kind: 'log-failure',
        problem: 'failure',
        title: 'Unable to log weight measurement',
        message: 'The weight measurement could not be logged: backend unavailable',
      });
      expect(presenter.loading).toBe(false);
      expect(presenter.dateControl.enabled).toBe(true);
      expect(presenter.weightForm.enabled).toBe(true);
      expect(presenter.canLog()).toBe(true);
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
    it('clears the form and returns a failed workflow to idle', async () => {
      const presenter = createPresenter(async () => {
        throw new Error('backend unavailable');
      });

      enterValidWeight(presenter);
      await firstValueFrom(presenter.log());

      expect(presenter.state().kind).toBe('log-failure');

      presenter.cancelLog();

      expect(presenter.state()).toEqual({ kind: 'idle' });
      expect(presenter.weightControl.value).toBeNull();
      expect(presenter.showWeightErrors()).toBe(false);
    });
  });

  function createPresenter(
    log: LogWeightMeasurementUseCase['log'] = async () => loggedResult,
  ): JournalLoggingPresenter {
    const logWeightMeasurementUseCase: LogWeightMeasurementUseCase = {
      log,
    };

    const getDefaultJournalDateUseCase: GetDefaultJournalDateUseCase = {
      get: () => ({
        kind: 'success',
        resultData: {
          defaultJournalDate: defaultDate,
        },
      }),
    };

    return new JournalLoggingPresenter(logWeightMeasurementUseCase, getDefaultJournalDateUseCase);
  }

  function enterValidWeight(presenter: JournalLoggingPresenter): void {
    presenter.weightControl.setValue(weightInKg);
  }
});

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
