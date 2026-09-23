import { describe, expect, it } from 'vitest';

import { stateKindsOf, type StateDimension } from './model';

type RequestState = StateDimension<
  'idle' | 'failure',
  {
    idle: undefined;
    failure: {
      readonly message: string;
    };
  }
>;

type DialogState = StateDimension<
  'closed' | 'open',
  {
    closed: undefined;
    open: undefined;
  }
>;

interface TestPageState {
  readonly requestState: RequestState;
  readonly dialogState: DialogState;
}

describe('pageStateToPageStateKind', () => {
  it('extracts the kind of every state dimension without retaining payloads', () => {
    const pageState: TestPageState = {
      requestState: {
        kind: 'failure',
        message: 'Unavailable',
      },
      dialogState: {
        kind: 'open',
      },
    };

    expect(stateKindsOf(pageState)).toEqual({
      requestState: 'failure',
      dialogState: 'open',
    });
  });
});
