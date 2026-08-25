import { type CalendarDate } from './calendar-date';
import type { CompletionStatus } from './completion-status';

export interface DailyRecord {
  readonly date: CalendarDate;
  readonly status: CompletionStatus;
}
