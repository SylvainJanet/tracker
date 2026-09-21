export interface LogWeightMeasurementSuccessfulView {
  readonly date: string;
  readonly weightInKg: number;
}

type LogWeightMeasurementProblem = 'failure';

export type JournalLoggingState =
  | { readonly kind: 'idle' }
  | { readonly kind: 'loading' }
  | {
      readonly kind: 'log-successful';
      readonly view: LogWeightMeasurementSuccessfulView;
    }
  | {
      readonly kind: 'log-failure';
      readonly problem: LogWeightMeasurementProblem;
      readonly title: string;
      readonly message: string;
    };

export interface LogWeightMeasurementTransition {
  readonly model: JournalLoggingModel;
  readonly effect:
    | {
        readonly kind: 'log';
      }
    | { readonly kind: 'none' };
}

export class JournalLoggingModel {
  private constructor(readonly state: JournalLoggingState) {}

  static initial(): JournalLoggingModel {
    return new JournalLoggingModel({ kind: 'idle' });
  }

  get loading(): boolean {
    return this.state.kind === 'loading';
  }

  public requestLog(): LogWeightMeasurementTransition {
    if (this.loading) {
      return {
        model: this,
        effect: {
          kind: 'none',
        },
      };
    }

    return {
      model: this.withLoadingState(),
      effect: {
        kind: 'log',
      },
    };
  }

  public cancelRequest(): JournalLoggingModel {
    return this.loading ? this : this.withIdleState();
  }

  public dateChanged(): JournalLoggingModel {
    return this.loading ? this : this.withIdleState();
  }

  public logSucceeded(view: LogWeightMeasurementSuccessfulView): JournalLoggingModel {
    return this.loading ? this.withSuccessfulViewState(view) : this;
  }

  public logFailed(error: unknown): JournalLoggingModel {
    if (!this.loading) {
      return this;
    }
    if (typeof error === 'string' || error instanceof Error) {
      const errorMessage = typeof error === 'string' ? error : error.message;
      return this.withFailureState(errorMessage);
    }
    return this.withFailureState('unknown error');
  }

  private withState(state: JournalLoggingState): JournalLoggingModel {
    return new JournalLoggingModel(state);
  }

  private withIdleState(): JournalLoggingModel {
    return new JournalLoggingModel({ kind: 'idle' });
  }

  private withLoadingState(): JournalLoggingModel {
    return this.withState({ kind: 'loading' });
  }

  private withSuccessfulViewState(view: LogWeightMeasurementSuccessfulView): JournalLoggingModel {
    return this.withState({ kind: 'log-successful', view });
  }

  private withFailureState(errorMessage: string): JournalLoggingModel {
    return this.withState({
      kind: 'log-failure',
      problem: 'failure',
      title: 'Unable to log weight measurement',
      message: `The weight measurement could not be logged: ${errorMessage}`,
    });
  }
}
