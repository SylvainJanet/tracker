import type { JournalLoggingState } from '../journal.logging.model.state';
import type { StateKinds } from '../../../../../../../../../shared/api/shared.component.model';

export const validStateKinds = new Set<StateKinds<JournalLoggingState>>([
  { logState: 'idle', getState: 'idle' },
  { logState: 'idle', getState: 'loading' },
  { logState: 'idle', getState: 'found' },
  { logState: 'idle', getState: 'not-found' },
  { logState: 'idle', getState: 'failure' },

  { logState: 'loading', getState: 'not-found' },

  { logState: 'logged', getState: 'idle' },

  { logState: 'failure', getState: 'not-found' },
]);
