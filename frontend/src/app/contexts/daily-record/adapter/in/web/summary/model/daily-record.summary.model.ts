import { type CalendarDate, parseCalendarDate } from '../../../../../domain/calendar-date';

export interface DailyRecordSummaryView {
  readonly date: string;
  readonly status: 'Completed' | 'In progress';
}

type DailyRecordSummaryProblem = 'failure' | 'invalid-date' | 'not-found';

type DailyRecordSummaryState =
  | { readonly kind: 'idle' }
  | { readonly kind: 'loading' }
  | {
      readonly kind: 'view';
      readonly view: DailyRecordSummaryView;
    }
  | {
      readonly kind: 'problem';
      readonly problem: DailyRecordSummaryProblem;
      readonly title: string;
      readonly message: string;
    };

interface DailyRecordSummarySelectionTransition {
  readonly model: DailyRecordSummaryModel;
  readonly effect:
    | {
        readonly kind: 'load';
        readonly query: CalendarDate;
      }
    | { readonly kind: 'none' };
}

export type DailyRecordSummaryReadModel = Pick<
  DailyRecordSummaryModel,
  'selectedDate' | 'state' | 'loading'
>;

export class DailyRecordSummaryModel {
  constructor(
    readonly selectedDate: string,
    readonly state: DailyRecordSummaryState,
  ) {}

  static initial(defaultDate: string): DailyRecordSummaryModel {
    return new DailyRecordSummaryModel(defaultDate, { kind: 'idle' });
  }

  get loading(): boolean {
    return this.state.kind === 'loading';
  }

  selectDate(intent: string): DailyRecordSummarySelectionTransition {
    if (this.loading) {
      return {
        model: this,
        effect: { kind: 'none' },
      };
    }

    const selectedModel = new DailyRecordSummaryModel(intent, {
      kind: 'idle',
    });
    const query = parseCalendarDate(intent);

    if (query === null) {
      return {
        model: selectedModel.withInvalidDateState(),
        effect: { kind: 'none' },
      };
    }

    return {
      model: selectedModel.withLoadingState(),
      effect: {
        kind: 'load',
        query,
      },
    };
  }

  recordFound(view: DailyRecordSummaryView): DailyRecordSummaryModel {
    if (!this.loading) {
      return this;
    }

    return this.withViewState(view);
  }

  recordNotFound(query: CalendarDate): DailyRecordSummaryModel {
    if (!this.loading) {
      return this;
    }

    return this.withNotFoundState(query);
  }

  recordLoadingFailed(): DailyRecordSummaryModel {
    if (!this.loading) {
      return this;
    }

    return this.withFailureState();
  }

  private withState(state: DailyRecordSummaryState): DailyRecordSummaryModel {
    return new DailyRecordSummaryModel(this.selectedDate, state);
  }

  private withLoadingState(): DailyRecordSummaryModel {
    return this.withState({ kind: 'loading' });
  }

  private withViewState(view: DailyRecordSummaryView): DailyRecordSummaryModel {
    return this.withState({ kind: 'view', view });
  }

  private withNotFoundState(query: CalendarDate): DailyRecordSummaryModel {
    return this.withState({
      kind: 'problem',
      problem: 'not-found',
      title: 'Daily record not found',
      message: `No daily record exists for ${query}.`,
    });
  }

  private withInvalidDateState(): DailyRecordSummaryModel {
    return this.withState({
      kind: 'problem',
      problem: 'invalid-date',
      title: 'Invalid date',
      message: 'Select a valid calendar date.',
    });
  }

  private withFailureState(): DailyRecordSummaryModel {
    return this.withState({
      kind: 'problem',
      problem: 'failure',
      title: 'Unable to load daily record',
      message: 'The daily record could not be loaded.',
    });
  }
}
