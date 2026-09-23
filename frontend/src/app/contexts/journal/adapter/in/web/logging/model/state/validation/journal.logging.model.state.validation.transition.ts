import type { JournalLoggingStateTransition } from '../journal.logging.model.state.transition';

export const validStateTransitions = new Set<JournalLoggingStateTransition>([
  {
    kind: 'get-load',
    transitions: [
      {
        from: { logState: 'idle', getState: 'idle' },
        to: [{ logState: 'idle', getState: 'loading' }],
      },
      {
        from: { logState: 'idle', getState: 'found' },
        to: [{ logState: 'idle', getState: 'loading' }],
      },
      {
        from: { logState: 'idle', getState: 'not-found' },
        to: [{ logState: 'idle', getState: 'loading' }],
      },
      {
        from: { logState: 'idle', getState: 'failure' },
        to: [{ logState: 'idle', getState: 'loading' }],
      },
      {
        from: { logState: 'logged', getState: 'idle' },
        to: [{ logState: 'idle', getState: 'loading' }],
      },
      {
        from: { logState: 'failure', getState: 'not-found' },
        to: [{ logState: 'idle', getState: 'loading' }],
      },
    ],
  },
  {
    kind: 'get-finish',
    transitions: [
      {
        from: { logState: 'idle', getState: 'loading' },
        to: [
          { logState: 'idle', getState: 'found' },
          { logState: 'idle', getState: 'not-found' },
          { logState: 'idle', getState: 'failure' },
        ],
      },
    ],
  },
  {
    kind: 'log-load',
    transitions: [
      {
        from: { logState: 'idle', getState: 'not-found' },
        to: [{ logState: 'loading', getState: 'not-found' }],
      },
      {
        from: { logState: 'failure', getState: 'not-found' },
        to: [{ logState: 'loading', getState: 'not-found' }],
      },
    ],
  },
  {
    kind: 'log-finish',
    transitions: [
      {
        from: { logState: 'loading', getState: 'not-found' },
        to: [
          { logState: 'logged', getState: 'idle' },
          { logState: 'failure', getState: 'not-found' },
        ],
      },
    ],
  },
  {
    kind: 'log-cancel',
    transitions: [
      {
        from: { logState: 'failure', getState: 'not-found' },
        to: [{ logState: 'idle', getState: 'not-found' }],
      },
    ],
  },
]);
