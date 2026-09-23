import type { JournalLoggingState } from './state/journal.logging.model.state';
import { validStateKinds } from './state/validation/journal.logging.model.state.validation.state';
import { validStateTransitions } from './state/validation/journal.logging.model.state.validation.transition';
import type {
  GetTransitionEffect,
  LogTransitionEffect,
} from './state/transition/journal.logging.model.state.transition.effect';
import type { GetSuccessfulView, LogSuccessfulView } from './view/journal.logging.model.view';
import {
  canTransition,
  ValidState,
  ValidStateTransition,
  ValidStateTransitionEffect,
} from '../../../../../../../shared/api/shared.component.model';

export class JournalLoggingModel {
  private constructor(readonly state: JournalLoggingState) {}

  static initial(): JournalLoggingModel {
    return new JournalLoggingModel({ logState: { kind: 'idle' }, getState: { kind: 'idle' } });
  }

  get anyLoading(): boolean {
    return this.state.logState.kind === 'loading' || this.state.getState.kind === 'loading';
  }

  get logLoading(): boolean {
    return this.state.logState.kind === 'loading';
  }

  get getLoading(): boolean {
    return this.state.getState.kind === 'loading';
  }

  @ValidState(validStateKinds)
  @ValidStateTransitionEffect('log-load', validStateTransitions)
  public logLoad(): LogTransitionEffect {
    if (!canTransition(this.state, 'log-load', validStateTransitions)) {
      return {
        kind: 'ignored',
        transition: this,
      };
    }

    return {
      kind: 'accepted',
      transition: this.withLogWeightLoadingState(),
      effect: {
        kind: 'log',
      },
    };
  }

  @ValidState(validStateKinds)
  @ValidStateTransitionEffect('get-load', validStateTransitions)
  public getLoad(): GetTransitionEffect {
    if (!canTransition(this.state, 'get-load', validStateTransitions)) {
      return {
        kind: 'ignored',
        transition: this,
      };
    }

    return {
      kind: 'accepted',
      transition: this.withGetWeightLoadingState(),
      effect: {
        kind: 'get',
      },
    };
  }

  @ValidState(validStateKinds)
  @ValidStateTransition('log-finish', validStateTransitions)
  public logFinishSuccess(view: LogSuccessfulView): JournalLoggingModel {
    return canTransition(this.state, 'log-finish', validStateTransitions)
      ? this.withSuccessfulLogWeightViewState(view)
      : this;
  }

  @ValidState(validStateKinds)
  @ValidStateTransition('get-finish', validStateTransitions)
  public getFinishSuccess(view: GetSuccessfulView): JournalLoggingModel {
    return canTransition(this.state, 'get-finish', validStateTransitions)
      ? this.withSuccessfulGetWeightViewState(view)
      : this;
  }

  @ValidState(validStateKinds)
  @ValidStateTransition('get-finish', validStateTransitions)
  public getFinishNotFound(): JournalLoggingModel {
    return canTransition(this.state, 'get-finish', validStateTransitions)
      ? this.withNotFoundGetWeightViewState()
      : this;
  }

  @ValidState(validStateKinds)
  @ValidStateTransition('log-finish', validStateTransitions)
  public logFinishFail(error: unknown): JournalLoggingModel {
    if (!canTransition(this.state, 'log-finish', validStateTransitions)) {
      return this;
    }
    if (typeof error === 'string' || error instanceof Error) {
      const errorMessage = typeof error === 'string' ? error : error.message;
      return this.withLogWeightFailureState(errorMessage);
    }
    return this.withLogWeightFailureState('unknown error');
  }

  @ValidState(validStateKinds)
  @ValidStateTransition('log-cancel', validStateTransitions)
  public logCancel(): JournalLoggingModel {
    if (!canTransition(this.state, 'log-cancel', validStateTransitions)) {
      return this;
    }
    return this.withState({
      logState: { kind: 'idle' },
      getState: this.state.getState,
    });
  }

  @ValidState(validStateKinds)
  @ValidStateTransition('get-finish', validStateTransitions)
  public getFinishFailed(error: unknown): JournalLoggingModel {
    if (!canTransition(this.state, 'get-finish', validStateTransitions)) {
      return this;
    }
    if (typeof error === 'string' || error instanceof Error) {
      const errorMessage = typeof error === 'string' ? error : error.message;
      return this.withGetWeightFailureState(errorMessage);
    }
    return this.withGetWeightFailureState('unknown error');
  }

  private withState(state: JournalLoggingState): JournalLoggingModel {
    return new JournalLoggingModel(state);
  }

  private withLogWeightLoadingState(): JournalLoggingModel {
    return this.withState({ logState: { kind: 'loading' }, getState: this.state.getState });
  }

  private withGetWeightLoadingState(): JournalLoggingModel {
    return this.withState({ getState: { kind: 'loading' }, logState: { kind: 'idle' } });
  }

  private withSuccessfulLogWeightViewState(view: LogSuccessfulView): JournalLoggingModel {
    return this.withState({
      logState: { kind: 'logged', view },
      getState: { kind: 'idle' },
    });
  }

  private withSuccessfulGetWeightViewState(view: GetSuccessfulView): JournalLoggingModel {
    return this.withState({
      getState: { kind: 'found', view },
      logState: this.state.logState,
    });
  }

  private withNotFoundGetWeightViewState(): JournalLoggingModel {
    return this.withState({
      logState: this.state.logState,
      getState: {
        kind: 'not-found',
        title: 'No weight measurement found',
        message: 'No weight measurement has been logged at this date.',
      },
    });
  }

  private withGetWeightFailureState(errorMessage: string): JournalLoggingModel {
    return this.withState({
      logState: this.state.logState,
      getState: {
        kind: 'failure',
        title: 'Unable to get weight measurement',
        message: `The weight measurement could not be retrieved: ${errorMessage}`,
      },
    });
  }

  private withLogWeightFailureState(errorMessage: string): JournalLoggingModel {
    return this.withState({
      logState: {
        kind: 'failure',
        title: 'Unable to log weight measurement',
        message: `The weight measurement could not be logged: ${errorMessage}`,
      },
      getState: this.state.getState,
    });
  }
}
