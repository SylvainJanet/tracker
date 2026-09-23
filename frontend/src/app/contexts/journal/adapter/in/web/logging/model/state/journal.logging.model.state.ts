import type { LogState, GetState } from './dimension/journal.logging.model.state.dimension';

export interface JournalLoggingState {
  readonly logState: LogState;
  readonly getState: GetState;
}
