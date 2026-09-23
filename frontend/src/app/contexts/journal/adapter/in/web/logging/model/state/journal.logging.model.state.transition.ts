import type { StateTransition } from '../../../../../../../../shared/api/shared.component.model';
import type { JournalLoggingState } from './journal.logging.model.state';

export const JournalLoggingStateTransitionKind = [
  'get-load',
  'get-finish',
  'log-load',
  'log-finish',
  'log-cancel',
] as const;

export type JournalLoggingStateTransition = StateTransition<
  JournalLoggingState,
  (typeof JournalLoggingStateTransitionKind)[number]
>;
