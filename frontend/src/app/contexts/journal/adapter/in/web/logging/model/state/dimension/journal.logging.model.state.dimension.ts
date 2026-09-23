import type { GetSuccessfulView, LogSuccessfulView } from '../../view/journal.logging.model.view';
import type { StateDimension } from '../../../../../../../../../shared/api/shared.component.model';

export const LogStateKind = ['idle', 'loading', 'logged', 'failure'] as const;
export const GetStateKind = ['idle', 'loading', 'found', 'not-found', 'failure'] as const;

interface LogStatePayloads {
  idle: undefined;
  loading: undefined;
  logged: {
    readonly view: LogSuccessfulView;
  };
  failure: {
    readonly title: string;
    readonly message: string;
  };
}

interface GetStatePayloads {
  idle: undefined;
  loading: undefined;
  found: {
    readonly view: GetSuccessfulView;
  };
  'not-found': {
    readonly title: string;
    readonly message: string;
  };
  failure: {
    readonly title: string;
    readonly message: string;
  };
}

export type LogState = StateDimension<(typeof LogStateKind)[number], LogStatePayloads>;
export type GetState = StateDimension<(typeof GetStateKind)[number], GetStatePayloads>;
