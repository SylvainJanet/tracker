import { describe, expect, it } from 'vitest';

import { JournalLoggingModel } from './journal.logging.model';
import type { GetSuccessfulView, LogSuccessfulView } from './view/journal.logging.model.view';

describe('JournalLoggingModel', () => {
  const getView: GetSuccessfulView = {
    date: '2026-09-20',
    weightInKg: 72.5,
  };

  const logView: LogSuccessfulView = {
    date: '2026-09-20',
    weightInKg: 72.5,
  };

  describe('initial', () => {
    it('starts with idle logging and lookup states', () => {
      const model = JournalLoggingModel.initial();

      expect(model.state).toEqual({
        logState: { kind: 'idle' },
        getState: { kind: 'idle' },
      });
      expect(model.anyLoading).toBe(false);
      expect(model.logLoading).toBe(false);
      expect(model.getLoading).toBe(false);
    });
  });

  describe('weight lookup', () => {
    it('accepts an initial lookup request', () => {
      const decision = JournalLoggingModel.initial().getLoad();

      expect(decision.kind).toBe('accepted');

      if (decision.kind === 'accepted') {
        expect(decision.effect).toEqual({ kind: 'get' });
        expect(decision.transition.state).toEqual({
          logState: { kind: 'idle' },
          getState: { kind: 'loading' },
        });
        expect(decision.transition.anyLoading).toBe(true);
        expect(decision.transition.getLoading).toBe(true);
        expect(decision.transition.logLoading).toBe(false);
      }
    });

    it('ignores another lookup request while one is loading', () => {
      const loadingModel = startGet();
      const decision = loadingModel.getLoad();

      expect(decision.kind).toBe('ignored');
      expect(decision.transition).toBe(loadingModel);
    });

    it('presents a found measurement', () => {
      const result = startGet().getFinishSuccess(getView);

      expect(result.state).toEqual({
        logState: { kind: 'idle' },
        getState: {
          kind: 'found',
          view: getView,
        },
      });
      expect(result.anyLoading).toBe(false);
    });

    it('presents a missing measurement', () => {
      const result = completeGetNotFound();

      expect(result.state).toEqual({
        logState: { kind: 'idle' },
        getState: {
          kind: 'not-found',
          title: 'No weight measurement found',
          message: 'No weight measurement has been logged at this date.',
        },
      });
    });

    it.each([
      ['a string', 'backend unavailable', 'backend unavailable'],
      ['an Error', new Error('network unavailable'), 'network unavailable'],
      ['an unknown value', { reason: 'unexpected' }, 'unknown error'],
    ])('presents a lookup failure from %s', (_description, error, expectedMessage) => {
      const result = startGet().getFinishFailed(error);

      expect(result.state).toEqual({
        logState: { kind: 'idle' },
        getState: {
          kind: 'failure',
          title: 'Unable to get weight measurement',
          message: `The weight measurement could not be retrieved: ${expectedMessage}`,
        },
      });
    });

    it.each([
      ['a found measurement', () => startGet().getFinishSuccess(getView)],
      ['a missing measurement', () => completeGetNotFound()],
      ['a lookup failure', () => startGet().getFinishFailed('backend unavailable')],
      ['a successful log', () => completeLog()],
      ['a logging failure', () => failLog()],
    ])('accepts a new lookup after %s', (_description, createModel) => {
      const decision = createModel().getLoad();

      expect(decision.kind).toBe('accepted');

      if (decision.kind === 'accepted') {
        expect(decision.effect).toEqual({ kind: 'get' });
        expect(decision.transition.state).toEqual({
          logState: { kind: 'idle' },
          getState: { kind: 'loading' },
        });
      }
    });

    it('rejects a lookup completion when no lookup is loading', () => {
      expect(() => JournalLoggingModel.initial().getFinishSuccess(getView)).toThrow(
        'Invalid model state transition',
      );
    });
  });

  describe('weight logging', () => {
    it('ignores logging before lookup confirms that the date is available', () => {
      const model = JournalLoggingModel.initial();
      const decision = model.logLoad();

      expect(decision.kind).toBe('ignored');
      expect(decision.transition).toBe(model);
    });

    it('accepts logging after lookup finds no measurement', () => {
      const decision = completeGetNotFound().logLoad();

      expect(decision.kind).toBe('accepted');

      if (decision.kind === 'accepted') {
        expect(decision.effect).toEqual({ kind: 'log' });
        expect(decision.transition.state).toEqual({
          logState: { kind: 'loading' },
          getState: {
            kind: 'not-found',
            title: 'No weight measurement found',
            message: 'No weight measurement has been logged at this date.',
          },
        });
        expect(decision.transition.logLoading).toBe(true);
      }
    });

    it('ignores another logging request while one is loading', () => {
      const loadingModel = startLog();
      const decision = loadingModel.logLoad();

      expect(decision.kind).toBe('ignored');
      expect(decision.transition).toBe(loadingModel);
    });

    it('presents the logged measurement', () => {
      const result = completeLog();

      expect(result.state).toEqual({
        logState: {
          kind: 'logged',
          view: logView,
        },
        getState: { kind: 'idle' },
      });
      expect(result.anyLoading).toBe(false);
    });

    it.each([
      ['a string', 'backend unavailable', 'backend unavailable'],
      ['an Error', new Error('network unavailable'), 'network unavailable'],
      ['an unknown value', { reason: 'unexpected' }, 'unknown error'],
    ])('presents a logging failure from %s', (_description, error, expectedMessage) => {
      const result = startLog().logFinishFail(error);

      expect(result.state).toEqual({
        logState: {
          kind: 'failure',
          title: 'Unable to log weight measurement',
          message: `The weight measurement could not be logged: ${expectedMessage}`,
        },
        getState: {
          kind: 'not-found',
          title: 'No weight measurement found',
          message: 'No weight measurement has been logged at this date.',
        },
      });
    });

    it('accepts a retry after logging fails', () => {
      const decision = failLog().logLoad();

      expect(decision.kind).toBe('accepted');

      if (decision.kind === 'accepted') {
        expect(decision.effect).toEqual({ kind: 'log' });
        expect(decision.transition.logLoading).toBe(true);
      }
    });

    it('cancels a failed logging attempt while preserving lookup knowledge', () => {
      const result = failLog().logCancel();

      expect(result.state).toEqual({
        logState: { kind: 'idle' },
        getState: {
          kind: 'not-found',
          title: 'No weight measurement found',
          message: 'No weight measurement has been logged at this date.',
        },
      });
    });

    it('rejects cancellation when logging has not failed', () => {
      expect(() => completeGetNotFound().logCancel()).toThrow('Invalid model state transition');
    });

    it('rejects a logging completion when logging is not loading', () => {
      expect(() => completeGetNotFound().logFinishSuccess(logView)).toThrow(
        'Invalid model state transition',
      );
    });
  });

  function startGet(model = JournalLoggingModel.initial()): JournalLoggingModel {
    const decision = model.getLoad();

    if (decision.kind !== 'accepted') {
      throw new Error('Expected the lookup request to be accepted');
    }

    return decision.transition;
  }

  function completeGetNotFound(): JournalLoggingModel {
    return startGet().getFinishNotFound();
  }

  function startLog(): JournalLoggingModel {
    const decision = completeGetNotFound().logLoad();

    if (decision.kind !== 'accepted') {
      throw new Error('Expected the logging request to be accepted');
    }

    return decision.transition;
  }

  function completeLog(): JournalLoggingModel {
    return startLog().logFinishSuccess(logView);
  }

  function failLog(): JournalLoggingModel {
    return startLog().logFinishFail('backend unavailable');
  }
});
