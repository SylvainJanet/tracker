import { type DailyRecord } from '../../../../../../domain/daily-record';
import { type DailyRecordLoggingView } from '../../model/daily-record.logging.model';
import type { CompletionStatus } from '../../../../../../domain/completion-status';

const STATUS_VIEWS = {
  IN_PROGRESS: 'In progress',
  COMPLETED: 'Completed',
} as const satisfies Readonly<Record<CompletionStatus, DailyRecordLoggingView['status']>>;

export class DailyRecordLoggingMapper {
  private constructor() {
    /* empty */
  }

  static toView(resultData: DailyRecord): DailyRecordLoggingView {
    return {
      date: resultData.date,
      status: STATUS_VIEWS[resultData.status],
    };
  }
}
