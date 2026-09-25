import { describe, expect, it } from 'vitest';

import { AnalysisWeightModel } from './analysis.weight.model';
import type { AnalysisWeightView } from './view/analysis.weight.model.view';

describe('AnalysisWeightModel', () => {
  const view: AnalysisWeightView = {
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

  it('starts in the initial state', () => {
    const model = AnalysisWeightModel.initial();

    expect(model.state).toEqual({
      analysisState: { kind: 'initial' },
    });
    expect(model.loading).toBe(false);
  });

  it('accepts the initial analysis request', () => {
    const decision = AnalysisWeightModel.initial().analysisLoad();

    expect(decision.kind).toBe('accepted');

    if (decision.kind === 'accepted') {
      expect(decision.effect).toEqual({ kind: 'analyze' });
      expect(decision.transition.state).toEqual({
        analysisState: { kind: 'loading' },
      });
      expect(decision.transition.loading).toBe(true);
    }
  });

  it('ignores another request while analysis is loading', () => {
    const loadingModel = startAnalysis();
    const decision = loadingModel.analysisLoad();

    expect(decision.kind).toBe('ignored');
    expect(decision.transition).toBe(loadingModel);
  });

  it('presents a successful analysis', () => {
    const result = startAnalysis().analysisFinishSuccessful(view);

    expect(result.state).toEqual({
      analysisState: {
        kind: 'analyzed',
        view,
      },
    });
    expect(result.loading).toBe(false);
  });

  it('presents an empty analysis', () => {
    const result = completeEmptyAnalysis();

    expect(result.state).toEqual({
      analysisState: {
        kind: 'empty',
        title: 'No weight measurement found',
        message: 'Nothing to display: no weight measurement was found.',
      },
    });
    expect(result.loading).toBe(false);
  });

  it.each([
    ['a string', 'backend unavailable', 'backend unavailable'],
    ['an Error', new Error('network unavailable'), 'network unavailable'],
    ['an unknown value', { reason: 'unexpected' }, 'unknown error'],
  ])('presents an analysis failure from %s', (_description, error, expectedMessage) => {
    const result = startAnalysis().analysisFinishFail(error);

    expect(result.state).toEqual({
      analysisState: {
        kind: 'failure',
        title: 'Unable to perform weight analysis',
        message: `The weight analysis could not be performed: ${expectedMessage}`,
      },
    });
    expect(result.loading).toBe(false);
  });

  it('accepts a retry after analysis fails', () => {
    const failedModel = startAnalysis().analysisFinishFail('backend unavailable');
    const decision = failedModel.analysisLoad();

    expect(decision.kind).toBe('accepted');

    if (decision.kind === 'accepted') {
      expect(decision.effect).toEqual({ kind: 'analyze' });
      expect(decision.transition.state).toEqual({
        analysisState: { kind: 'loading' },
      });
    }
  });

  it('treats the empty state as valid and ignores another analysis request', () => {
    const emptyModel = completeEmptyAnalysis();
    const decision = emptyModel.analysisLoad();

    expect(decision.kind).toBe('ignored');
    expect(decision.transition).toBe(emptyModel);
  });

  it('rejects an analysis completion when no analysis is loading', () => {
    expect(() => AnalysisWeightModel.initial().analysisFinishSuccessful(view)).toThrow(
      'Invalid model state transition',
    );
  });

  function startAnalysis(): AnalysisWeightModel {
    const decision = AnalysisWeightModel.initial().analysisLoad();

    if (decision.kind !== 'accepted') {
      throw new Error('Expected the analysis request to be accepted');
    }

    return decision.transition;
  }

  function completeEmptyAnalysis(): AnalysisWeightModel {
    return startAnalysis().analysisFinishEmpty();
  }
});
