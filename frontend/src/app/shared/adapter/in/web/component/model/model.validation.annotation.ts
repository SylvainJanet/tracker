import {
  type StateDimension,
  type StateKinds,
  stateKindsOf,
  type StateKindsTransition,
  type StateTransition,
  type TransitionEffect,
} from './model';

function isValidStateKind<
  State extends {
    [K in keyof State]: StateDimension<string, Record<string, unknown> & Record<never, never>>;
  },
>(stateKind: StateKinds<State>, validStateKinds: Set<StateKinds<State>>): boolean {
  return [...validStateKinds].some((valid) => {
    return Object.keys(valid).every(
      (key) => valid[key as keyof State] === stateKind[key as keyof State],
    );
  });
}

function isInStateKindsTransition<
  State extends {
    [K in keyof State]: StateDimension<string, Record<string, unknown> & Record<never, never>>;
  },
>(
  fromStateKinds: StateKinds<State>,
  toStateKinds: StateKinds<State>,
  stateKindsTransitions: StateKindsTransition<State>[],
): boolean {
  return stateKindsTransitions.some((transition) => {
    const validFrom = transition.from;
    const validTos = transition.to;
    return (
      Object.keys(validFrom).every(
        (key) => validFrom[key as keyof State] === fromStateKinds[key as keyof State],
      ) &&
      validTos.some((validTo) => {
        return Object.keys(validTo).every(
          (key) => validTo[key as keyof State] === toStateKinds[key as keyof State],
        );
      })
    );
  });
}

function isValidStateKindsTransition<
  State extends {
    [K in keyof State]: StateDimension<string, Record<string, unknown> & Record<never, never>>;
  },
>(
  fromStateKinds: StateKinds<State>,
  toStateKinds: StateKinds<State>,
  validStateTransitions: Set<StateTransition<State>>,
  stateTransitionKind: string,
): boolean {
  const expectedStateTransitions = [...validStateTransitions].find(
    (validTransition) => validTransition.kind === stateTransitionKind,
  )?.transitions;
  if (!expectedStateTransitions) {
    throw new Error(`No valid state transition found for kind: ${stateTransitionKind}`);
  }

  return isInStateKindsTransition(fromStateKinds, toStateKinds, expectedStateTransitions);
}

export function ValidState<
  Model extends {
    readonly state: {
      [K in keyof Model['state']]: StateDimension<
        string,
        Record<string, unknown> & Record<never, never>
      >;
    };
  },
>(validPageStateKinds: Set<StateKinds<Model['state']>>) {
  return function ValidState<Args extends unknown[], Result>(
    _targetWeight: object,
    _methodName: string | symbol,
    descriptor: TypedPropertyDescriptor<(...args: Args) => Result>,
  ): void {
    const original = descriptor.value;

    if (!original) {
      throw new Error('Expected a method');
    }

    descriptor.value = function (this: Model, ...args: Args): Result {
      const thisStateKind = stateKindsOf(this.state);
      if (!isValidStateKind(thisStateKind, validPageStateKinds)) {
        throw new Error('Invalid model state: ' + JSON.stringify(thisStateKind));
      }
      return original.apply(this, args);
    };
  };
}

export function ValidStateTransition<
  Model extends {
    readonly state: {
      [K in keyof Model['state']]: StateDimension<
        string,
        Record<string, unknown> & Record<never, never>
      >;
    };
  },
  Transition extends StateTransition<Model['state']>,
>(kind: NoInfer<Transition['kind']>, validStateTransitions: Set<Transition>) {
  return function ValidStateTransition<Args extends unknown[]>(
    _targetWeight: object,
    _methodName: string | symbol,
    descriptor: TypedPropertyDescriptor<(...args: Args) => Model>,
  ): void {
    const original = descriptor.value;

    if (!original) {
      throw new Error('Expected a method');
    }

    descriptor.value = function (this: Model, ...args: Args): Model {
      const oldStateKinds = stateKindsOf(this.state);

      const newModel = original.apply(this, args);
      const newStateKinds = stateKindsOf(newModel.state);

      if (!isValidStateKindsTransition(oldStateKinds, newStateKinds, validStateTransitions, kind)) {
        throw new Error(
          'Invalid model state transition for transition kind: ' +
            kind +
            ': ' +
            JSON.stringify(oldStateKinds) +
            ' -> ' +
            JSON.stringify(newStateKinds) +
            '. The only valid transitions for this transition kind are: ' +
            JSON.stringify(
              [...validStateTransitions]
                .filter((pt) => pt.kind === kind)
                .map((pt) => pt.transitions),
            ),
        );
      }
      return newModel;
    };
  };
}

export function ValidStateTransitionEffect<
  Model extends {
    readonly state: {
      [K in keyof Model['state']]: StateDimension<
        string,
        Record<string, unknown> & Record<never, never>
      >;
    };
  },
  Transition extends StateTransition<Model['state']>,
  Effect extends TransitionEffect<string, Record<string, unknown>, Model>,
>(kind: NoInfer<Transition['kind']>, validStateTransitions: Set<Transition>) {
  return function ValidStateTransitionEffect<Args extends unknown[]>(
    _targetWeight: object,
    _methodName: string | symbol,
    descriptor: TypedPropertyDescriptor<(...args: Args) => Effect>,
  ): void {
    const original = descriptor.value;

    if (!original) {
      throw new Error('Expected a method');
    }

    descriptor.value = function (this: Model, ...args: Args): Effect {
      const oldStateKinds = stateKindsOf(this.state);

      const result = original.apply(this, args);
      if (result.kind === 'ignored') {
        if (result.transition !== this) {
          throw new Error('An ignored event must preserve the current model');
        }
        return result;
      }

      const newModel = result.transition;

      const newStateKinds = stateKindsOf(newModel.state);
      if (!isValidStateKindsTransition(oldStateKinds, newStateKinds, validStateTransitions, kind)) {
        throw new Error(
          'Invalid model state transition for transition kind: ' +
            kind +
            ': ' +
            JSON.stringify(oldStateKinds) +
            ' -> ' +
            JSON.stringify(newStateKinds) +
            '. The only valid transitions for this transition kind are: ' +
            JSON.stringify(
              [...validStateTransitions]
                .filter((pt) => pt.kind === kind)
                .map((pt) => pt.transitions),
            ),
        );
      }
      return result;
    };
  };
}

export function canTransition<
  State extends {
    [K in keyof State]: StateDimension<string, Record<string, unknown> & Record<never, never>>;
  },
  Transition extends StateTransition<State>,
>(
  state: State,
  kind: NoInfer<Transition['kind']>,
  validStateTransitions: Set<Transition>,
): boolean {
  const oldStateKinds = stateKindsOf(state);
  const expectedTransition = [...validStateTransitions].find(
    (validTransition) => validTransition.kind === kind,
  );
  if (!expectedTransition) {
    throw new Error(`No valid state transition found for kind: ${kind}`);
  }

  return expectedTransition.transitions.some((transition) => {
    return Object.keys(transition.from).every(
      (key) => transition.from[key as keyof State] === oldStateKinds[key as keyof State],
    );
  });
}
