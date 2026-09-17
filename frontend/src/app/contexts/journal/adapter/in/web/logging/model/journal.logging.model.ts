import {
  type CalendarDate,
  isCalendarDate,
  parseCalendarDate,
} from '../../../../../domain/calendar-date';
import { isWeight, parseWeight } from '../../../../../domain/weight';
import { type DailyRecord, dailyRecord } from '../../../../../domain/daily-record';

export interface DailyRecordLoggingView {
  readonly date: string;
  readonly weight: number;
}

type DailyRecordLoggingProblem = 'already-exists' | 'failure' | 'invalid-date' | 'invalid-weight';

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
  readonly model: JournalLoggingModel;
  readonly effect:
    | {
        readonly kind: 'create';
        readonly command: DailyRecord;
      }
    | { readonly kind: 'none' };
}

export type DailyRecordLoggingReadModel = Pick<
  JournalLoggingModel,
  'selectedDate' | 'state' | 'loading' | 'canCreate' | 'selectWeight'
>;

export class JournalLoggingModel {
  constructor(
    readonly selectedDate: string,
    readonly selectedWeight: number | null = null,
    readonly state: DailyRecordLoggingState,
  ) {}

  static initial(defaultDate: string): JournalLoggingModel {
    return new JournalLoggingModel(defaultDate, null, { kind: 'idle' });
  }

  get loading(): boolean {
    return this.state.kind === 'loading';
  }

  get canCreate(): boolean {
    return (
      isCalendarDate(this.selectedDate) &&
      this.selectedWeight !== null &&
      isWeight(this.selectedWeight) &&
      !this.loading
    );
  }

  requestCreation(): DailyRecordLoggingCreationTransition {
    if (this.loading) {
      return {
        model: this,
        effect: { kind: 'none' },
      };
    }

    const rawDate = parseCalendarDate(this.selectedDate);
    const rawWeight = this.selectedWeight === null ? null : parseWeight(this.selectedWeight);

    if (rawDate === null) {
      return {
        model: this.withInvalidDateState(),
        effect: { kind: 'none' },
      };
    }

    if (rawWeight === null) {
      return {
        model: this.withInvalidWeightState(),
        effect: { kind: 'none' },
      };
    }

    console.log(rawDate);
    console.log(rawWeight);
    const command = dailyRecord(rawDate, rawWeight);

    return {
      model: this.withLoadingState(),
      effect: {
        kind: 'create',
        command,
      },
    };
  }

  selectDate(intent: string): JournalLoggingModel {
    if (this.loading || intent === this.selectedDate) {
      return this;
    }

    return new JournalLoggingModel(intent, this.selectedWeight, { kind: 'idle' });
  }

  selectWeight(number: number): JournalLoggingModel {
    if (this.loading || number === this.selectedWeight) {
      return this;
    }

    return new JournalLoggingModel(this.selectedDate, number, { kind: 'idle' });
  }

  creationSucceeded(view: DailyRecordLoggingView): JournalLoggingModel {
    if (!this.loading) {
      return this;
    }

    return this.withViewState(view);
  }

  recordAlreadyExists(date: CalendarDate): JournalLoggingModel {
    if (!this.loading) {
      return this;
    }

    return this.withAlreadyExistsState(date);
  }

  creationFailed(): JournalLoggingModel {
    if (!this.loading) {
      return this;
    }

    return this.withFailureState();
  }

  private withState(state: DailyRecordLoggingState): JournalLoggingModel {
    return new JournalLoggingModel(this.selectedDate, this.selectedWeight, state);
  }

  private withLoadingState(): JournalLoggingModel {
    return this.withState({ kind: 'loading' });
  }

  private withViewState(view: DailyRecordLoggingView): JournalLoggingModel {
    return this.withState({ kind: 'view', view });
  }

  private withAlreadyExistsState(date: CalendarDate): JournalLoggingModel {
    return this.withState({
      kind: 'problem',
      problem: 'already-exists',
      title: 'Daily record already exists',
      message: `A daily record already exists for ${date}.`,
    });
  }

  private withInvalidDateState(): JournalLoggingModel {
    return this.withState({
      kind: 'problem',
      problem: 'invalid-date',
      title: 'Invalid date',
      message: `Select a valid calendar date.`,
    });
  }

  private withInvalidWeightState(): JournalLoggingModel {
    return this.withState({
      kind: 'problem',
      problem: 'invalid-weight',
      title: 'Invalid weight',
      message: `Select a valid weight.`,
    });
  }

  private withFailureState(): JournalLoggingModel {
    return this.withState({
      kind: 'problem',
      problem: 'failure',
      title: 'Unable to create daily record',
      message: 'The daily record could not be created.',
    });
  }
}
