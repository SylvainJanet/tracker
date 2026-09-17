import { type CalendarDate, parseCalendarDate } from '../../../../../domain/calendar-date';

export interface DailyRecordSummaryView {
  readonly date: string;
}

type DailyRecordSummaryState =
  | {
      readonly kind: 'view';
      readonly view: DailyRecordSummaryView;
    }
  | {
      readonly kind: 'problem';
      readonly problem: 'invalid-date';
      readonly title: string;
      readonly message: string;
    };

interface DailyRecordSummarySelectionTransition {
  readonly model: JournalSummaryModel;
  readonly effect:
    | { readonly kind: 'none' }
    | {
        readonly kind: 'load';
        readonly query: CalendarDate;
      };
}

export type DailyRecordSummaryReadModel = Pick<JournalSummaryModel, 'selectedDate' | 'state'>;

export class JournalSummaryModel {
  constructor(
    readonly selectedDate: string,
    readonly state: DailyRecordSummaryState,
  ) {}

  static initial(defaultDate: string): JournalSummaryModel {
    return new JournalSummaryModel(defaultDate, { kind: 'view', view: { date: defaultDate } });
  }

  selectDate(intent: string): DailyRecordSummarySelectionTransition {
    const selectedModel = new JournalSummaryModel(intent, {
      kind: 'view',
      view: { date: intent },
    });
    const query = parseCalendarDate(intent);

    if (query === null) {
      return {
        model: selectedModel.withInvalidDateState(),
        effect: { kind: 'none' },
      };
    }

    return {
      model: selectedModel.withViewState({ date: intent }),
      effect: { kind: 'none' },
    };
  }

  recordFound(view: DailyRecordSummaryView): JournalSummaryModel {
    return this.withViewState(view);
  }

  private withState(state: DailyRecordSummaryState): JournalSummaryModel {
    return new JournalSummaryModel(this.selectedDate, state);
  }

  private withViewState(view: DailyRecordSummaryView): JournalSummaryModel {
    return this.withState({ kind: 'view', view });
  }

  private withInvalidDateState(): JournalSummaryModel {
    return this.withState({
      kind: 'problem',
      problem: 'invalid-date',
      title: 'Invalid date',
      message: 'Select a valid calendar date.',
    });
  }
}
