import { describe, expect, it } from 'vitest';

import {
  JournalLoggingModel,
  type LogWeightMeasurementSuccessfulView,
} from './journal.logging.model';

describe('JournalLoggingModel', () => {
  const successfulView: LogWeightMeasurementSuccessfulView = {
    date: '2026-09-20',
    weightInKg: 72.5,
  };

  describe('initial', () => {
    it('creates an idle model', () => {
      const model = JournalLoggingModel.initial();

      expect(model.state).toEqual({ kind: 'idle' });
      expect(model.loading).toBe(false);
    });
  });

  describe('requestLog', () => {
    it('starts loading and requests the log effect', () => {
      const model = JournalLoggingModel.initial();

      const transition = model.requestLog();

      expect(transition.model.state).toEqual({ kind: 'loading' });
      expect(transition.model.loading).toBe(true);
      expect(transition.effect).toEqual({ kind: 'log' });
    });

    it('does nothing when already loading', () => {
      const loadingModel = requestLog();

      const transition = loadingModel.requestLog();

      expect(transition.model.state).toEqual({ kind: 'loading' });
      expect(transition.model.loading).toBe(true);
      expect(transition.effect).toEqual({ kind: 'none' });
    });

    it('allows another request after a successful log', () => {
      const successfulModel = completeLogging();

      const transition = successfulModel.requestLog();

      expect(transition.model.state).toEqual({ kind: 'loading' });
      expect(transition.effect).toEqual({ kind: 'log' });
    });

    it('allows another request after a failed log', () => {
      const failedModel = failLogging('backend unavailable');

      const transition = failedModel.requestLog();

      expect(transition.model.state).toEqual({ kind: 'loading' });
      expect(transition.effect).toEqual({ kind: 'log' });
    });
  });

  describe('cancelRequest', () => {
    it('returns an idle model when no log is loading', () => {
      const successfulModel = completeLogging();

      const result = successfulModel.cancelRequest();

      expect(result.state).toEqual({ kind: 'idle' });
      expect(result.loading).toBe(false);
    });

    it('returns to idle after a failure', () => {
      const failedModel = failLogging('backend unavailable');

      const result = failedModel.cancelRequest();

      expect(result.state).toEqual({ kind: 'idle' });
    });

    it('does not cancel a loading request', () => {
      const loadingModel = requestLog();

      const result = loadingModel.cancelRequest();

      expect(result.state).toEqual({ kind: 'loading' });
      expect(result.loading).toBe(true);
    });
  });

  describe('dateChanged', () => {
    it('returns to idle after the selected date changes', () => {
      const successfulModel = completeLogging();

      const result = successfulModel.dateChanged();

      expect(result.state).toEqual({ kind: 'idle' });
      expect(result.loading).toBe(false);
    });

    it('preserves an active logging request', () => {
      const loadingModel = requestLog();

      const result = loadingModel.dateChanged();

      expect(result.state).toEqual({ kind: 'loading' });
      expect(result.loading).toBe(true);
    });
  });

  describe('logSucceeded', () => {
    it('presents the successful view when loading', () => {
      const loadingModel = requestLog();

      const result = loadingModel.logSucceeded(successfulView);

      expect(result.state).toEqual({
        kind: 'log-successful',
        view: successfulView,
      });
      expect(result.loading).toBe(false);
    });

    it('ignores a success when no log is loading', () => {
      const idleModel = JournalLoggingModel.initial();

      const result = idleModel.logSucceeded(successfulView);

      expect(result.state).toEqual({ kind: 'idle' });
      expect(result.loading).toBe(false);
    });

    it('ignores a stale success after a failure', () => {
      const failedModel = failLogging('backend unavailable');

      const result = failedModel.logSucceeded(successfulView);

      expect(result.state).toEqual(failedModel.state);
    });
  });

  describe('logFailed', () => {
    it('presents a string failure when loading', () => {
      const result = requestLog().logFailed('backend unavailable');

      expect(result.state).toEqual({
        kind: 'log-failure',
        problem: 'failure',
        title: 'Unable to log weight measurement',
        message: 'The weight measurement could not be logged: backend unavailable',
      });
      expect(result.loading).toBe(false);
    });

    it('uses the message from an Error', () => {
      const result = requestLog().logFailed(new Error('network unavailable'));

      expect(result.state).toEqual({
        kind: 'log-failure',
        problem: 'failure',
        title: 'Unable to log weight measurement',
        message: 'The weight measurement could not be logged: network unavailable',
      });
    });

    it('uses a fallback for an unknown error value', () => {
      const result = requestLog().logFailed({ reason: 'unexpected' });

      expect(result.state).toEqual({
        kind: 'log-failure',
        problem: 'failure',
        title: 'Unable to log weight measurement',
        message: 'The weight measurement could not be logged: unknown error',
      });
    });

    it('ignores a failure when no log is loading', () => {
      const idleModel = JournalLoggingModel.initial();

      const result = idleModel.logFailed('backend unavailable');

      expect(result.state).toEqual({ kind: 'idle' });
      expect(result.loading).toBe(false);
    });

    it('ignores a stale failure after success', () => {
      const successfulModel = completeLogging();

      const result = successfulModel.logFailed('late failure');

      expect(result.state).toEqual(successfulModel.state);
    });
  });

  function requestLog(): JournalLoggingModel {
    return JournalLoggingModel.initial().requestLog().model;
  }

  function completeLogging(): JournalLoggingModel {
    return requestLog().logSucceeded(successfulView);
  }

  function failLogging(error: unknown): JournalLoggingModel {
    return requestLog().logFailed(error);
  }
});
