import { type DailyRecord } from '../../../../../../domain/daily-record';
import { type DailyRecordSummaryView } from '../../model/daily-record.summary.model';
import type { CompletionStatus } from '../../../../../../domain/completion-status';

const STATUS_VIEWS = {
  IN_PROGRESS: 'In progress',
  COMPLETED: 'Completed',
} as const satisfies Readonly<Record<CompletionStatus, DailyRecordSummaryView['status']>>;

export class DailyRecordSummaryMapper {
  private constructor() {
    /* empty */
  }

  static toView(resultData: DailyRecord): DailyRecordSummaryView {
    return {
      date: resultData.date,
      status: STATUS_VIEWS[resultData.status],
    };
  }
}
