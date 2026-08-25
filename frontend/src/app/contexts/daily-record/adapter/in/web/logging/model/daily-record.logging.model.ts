import {
  type CalendarDate,
  isCalendarDate,
  parseCalendarDate,
} from '../../../../../domain/calendar-date';

export interface DailyRecordLoggingView {
  readonly date: string;
  readonly status: 'Completed' | 'In progress';
}

type DailyRecordLoggingProblem = 'already-exists' | 'failure' | 'invalid-date';

type DailyRecordLoggingState =
  | { readonly kind: 'idle' }
  | { readonly kind: 'loading' }
  | {
      readonly kind: 'view';
      readonly view: DailyRecordLoggingView;
    }
  | {
      readonly kind: 'problem';
      readonly problem: DailyRecordLoggingProblem;
      readonly title: string;
      readonly message: string;
    };

interface DailyRecordLoggingCreationTransition {
  readonly model: DailyRecordLoggingModel;
  readonly effect:
    | {
        readonly kind: 'create';
        readonly command: CalendarDate;
      }
    | { readonly kind: 'none' };
}

export type DailyRecordLoggingReadModel = Pick<
  DailyRecordLoggingModel,
  'selectedDate' | 'state' | 'loading' | 'canCreate'
>;

export class DailyRecordLoggingModel {
  constructor(
    readonly selectedDate: string,
    readonly state: DailyRecordLoggingState,
  ) {}

  static initial(defaultDate: string): DailyRecordLoggingModel {
    return new DailyRecordLoggingModel(defaultDate, { kind: 'idle' });
  }

  get loading(): boolean {
    return this.state.kind === 'loading';
  }

  get canCreate(): boolean {
    return isCalendarDate(this.selectedDate) && !this.loading;
  }

  requestCreation(): DailyRecordLoggingCreationTransition {
    if (this.loading) {
      return {
        model: this,
        effect: { kind: 'none' },
      };
    }

    const command = parseCalendarDate(this.selectedDate);

    if (command === null) {
      return {
        model: this.withInvalidDateState(),
        effect: { kind: 'none' },
      };
    }

    return {
      model: this.withLoadingState(),
      effect: {
        kind: 'create',
        command,
      },
    };
  }

  selectDate(intent: string): DailyRecordLoggingModel {
    if (this.loading || intent === this.selectedDate) {
      return this;
    }

    return new DailyRecordLoggingModel(intent, { kind: 'idle' });
  }

  creationSucceeded(view: DailyRecordLoggingView): DailyRecordLoggingModel {
    if (!this.loading) {
      return this;
    }

    return this.withViewState(view);
  }

  recordAlreadyExists(date: CalendarDate): DailyRecordLoggingModel {
    if (!this.loading) {
      return this;
    }

    return this.withAlreadyExistsState(date);
  }

  creationFailed(): DailyRecordLoggingModel {
    if (!this.loading) {
      return this;
    }

    return this.withFailureState();
  }

  private withState(state: DailyRecordLoggingState): DailyRecordLoggingModel {
    return new DailyRecordLoggingModel(this.selectedDate, state);
  }

  private withLoadingState(): DailyRecordLoggingModel {
    return this.withState({ kind: 'loading' });
  }

  private withViewState(view: DailyRecordLoggingView): DailyRecordLoggingModel {
    return this.withState({ kind: 'view', view });
  }

  private withAlreadyExistsState(date: CalendarDate): DailyRecordLoggingModel {
    return this.withState({
      kind: 'problem',
      problem: 'already-exists',
      title: 'Daily record already exists',
      message: `A daily record already exists for ${date}.`,
    });
  }

  private withInvalidDateState(): DailyRecordLoggingModel {
    return this.withState({
      kind: 'problem',
      problem: 'invalid-date',
      title: 'Invalid date',
      message: `Select a valid calendar date.`,
    });
  }

  private withFailureState(): DailyRecordLoggingModel {
    return this.withState({
      kind: 'problem',
      problem: 'failure',
      title: 'Unable to create daily record',
      message: 'The daily record could not be created.',
    });
  }
}
