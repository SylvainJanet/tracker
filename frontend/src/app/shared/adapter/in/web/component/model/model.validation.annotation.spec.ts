import { describe, expect, it } from 'vitest';

import type { StateDimension, StateKinds, StateTransition, TransitionEffect } from './model';
import {
  canTransition,
  ValidState,
  ValidStateTransition,
  ValidStateTransitionEffect,
} from './model.validation.annotation';

type RequestState = StateDimension<
  'idle' | 'loading' | 'complete',
  {
    idle: undefined;
    loading: undefined;
    complete: {
      readonly value: string;
    };
  }
>;

interface TestPageState {
  readonly requestState: RequestState;
}

type TestPageTransition = StateTransition<TestPageState, 'start' | 'finish'>;

const validPageStateKinds = new Set<StateKinds<TestPageState>>([
  { requestState: 'idle' },
  { requestState: 'loading' },
  { requestState: 'complete' },
]);

const validPageTransitions = new Set<TestPageTransition>([
  {
    kind: 'start',
    transitions: [
      {
        from: { requestState: 'idle' },
        to: [{ requestState: 'loading' }],
      },
    ],
  },
  {
    kind: 'finish',
    transitions: [
      {
        from: { requestState: 'loading' },
        to: [{ requestState: 'complete' }],
      },
    ],
  },
]);

type TestTransitionEffect = TransitionEffect<
  'load',
  {
    load: undefined;
  },
  TestModel
>;

class TestModel {
  private constructor(readonly state: TestPageState) {}

  static idle(): TestModel {
    return new TestModel({ requestState: { kind: 'idle' } });
  }

  static loading(): TestModel {
    return new TestModel({ requestState: { kind: 'loading' } });
  }

  static complete(): TestModel {
    return new TestModel({
      requestState: {
        kind: 'complete',
        value: 'result',
      },
    });
  }

  static invalid(): TestModel {
    return new TestModel({
      requestState: {
        kind: 'invalid',
      } as unknown as RequestState,
    });
  }

  @ValidState(validPageStateKinds)
  assertValidState(): TestModel {
    return this;
  }

  @ValidStateTransition('start', validPageTransitions)
  transitionToLoading(): TestModel {
    return TestModel.loading();
  }

  @ValidStateTransition('start', validPageTransitions)
  transitionDirectlyToComplete(): TestModel {
    return TestModel.complete();
  }

  @ValidStateTransition('start', validPageTransitions)
  start(): TestModel {
    return TestModel.loading();
  }

  @ValidStateTransition('finish', validPageTransitions)
  startUsingFinishKind(): TestModel {
    return TestModel.loading();
  }

  @ValidStateTransition('missing' as TestPageTransition['kind'], validPageTransitions)
  transitionUsingUnknownKind(): TestModel {
    return TestModel.loading();
  }

  @ValidStateTransitionEffect('start', validPageTransitions)
  acceptStart(): TestTransitionEffect {
    return {
      kind: 'accepted',
      effect: { kind: 'load' },
      transition: TestModel.loading(),
    };
  }

  @ValidStateTransitionEffect('start', validPageTransitions)
  acceptInvalidTransition(): TestTransitionEffect {
    return {
      kind: 'accepted',
      effect: { kind: 'load' },
      transition: TestModel.complete(),
    };
  }

  @ValidStateTransitionEffect('start', validPageTransitions)
  ignore(): TestTransitionEffect {
    return {
      kind: 'ignored',
      transition: this,
    };
  }

  @ValidStateTransitionEffect('start', validPageTransitions)
  ignoreWithReplacement(): TestTransitionEffect {
    return {
      kind: 'ignored',
      transition: TestModel.idle(),
    };
  }
}

describe('model state validation annotations', () => {
  describe('ValidState', () => {
    it('accepts a configured state kind', () => {
      const model = TestModel.idle();

      expect(model.assertValidState()).toBe(model);
    });

    it('rejects a state kind outside the configured state space', () => {
      expect(() => TestModel.invalid().assertValidState()).toThrow('Invalid model state');
    });
  });

  describe('ValidStateTransition edge validation', () => {
    it('accepts a configured edge', () => {
      expect(TestModel.idle().transitionToLoading().state).toEqual({
        requestState: { kind: 'loading' },
      });
    });

    it('rejects an edge absent from every transition kind', () => {
      expect(() => TestModel.idle().transitionDirectlyToComplete()).toThrow(
        'Invalid model state transition',
      );
    });
  });

  describe('ValidStateTransition kind validation', () => {
    it('accepts an edge configured for the requested transition kind', () => {
      expect(TestModel.idle().start().state).toEqual({
        requestState: { kind: 'loading' },
      });
    });

    it('rejects an edge belonging to another transition kind', () => {
      expect(() => TestModel.idle().startUsingFinishKind()).toThrow(
        'Invalid model state transition',
      );
    });

    it('rejects an unknown transition kind', () => {
      expect(() => TestModel.idle().transitionUsingUnknownKind()).toThrow(
        'No valid state transition found for kind: missing',
      );
    });
  });

  describe('ValidStateTransitionEffect', () => {
    it('accepts an effect whose transition matches its kind', () => {
      const result = TestModel.idle().acceptStart();

      expect(result.kind).toBe('accepted');

      if (result.kind === 'accepted') {
        expect(result.effect).toEqual({ kind: 'load' });
        expect(result.transition.state).toEqual({
          requestState: { kind: 'loading' },
        });
      }
    });

    it('rejects an accepted effect whose transition does not match its kind', () => {
      expect(() => TestModel.idle().acceptInvalidTransition()).toThrow(
        'Invalid model state transition for transition kind: start',
      );
    });

    it('allows an ignored effect that preserves the current model', () => {
      const model = TestModel.idle();
      const result = model.ignore();

      expect(result).toEqual({
        kind: 'ignored',
        transition: model,
      });
    });

    it('rejects an ignored effect that replaces the current model', () => {
      expect(() => TestModel.idle().ignoreWithReplacement()).toThrow(
        'An ignored event must preserve the current model',
      );
    });
  });
});

describe('canTransition', () => {
  it('returns true when the transition has an edge from the current state', () => {
    expect(canTransition(TestModel.idle().state, 'start', validPageTransitions)).toBe(true);
  });

  it('returns false when the transition has no edge from the current state', () => {
    expect(canTransition(TestModel.loading().state, 'start', validPageTransitions)).toBe(false);
  });

  it('rejects an unknown transition kind', () => {
    expect(() =>
      canTransition(
        TestModel.idle().state,
        'missing' as TestPageTransition['kind'],
        validPageTransitions,
      ),
    ).toThrow('No valid state transition found for kind: missing');
  });
});
