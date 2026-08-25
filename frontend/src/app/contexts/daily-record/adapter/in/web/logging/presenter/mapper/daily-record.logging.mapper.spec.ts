import { calendarDate } from '../../../../../../domain/calendar-date';
import { type DailyRecord } from '../../../../../../domain/daily-record';
import { type DailyRecordLoggingView } from '../../model/daily-record.logging.model';
import { DailyRecordLoggingMapper } from './daily-record.logging.mapper';
import type { CompletionStatus } from '../../../../../../domain/completion-status';

const statusCases = [
  ['IN_PROGRESS', 'In progress'],
  ['COMPLETED', 'Completed'],
] as const satisfies readonly (readonly [CompletionStatus, DailyRecordLoggingView['status']])[];

describe('DailyRecordLoggingMapper', () => {
  describe('toView', () => {
    it.each(statusCases)(
      'maps a %s daily record to page-specific display language',
      (status, expectedStatus) => {
        const resultData = {
          date: calendarDate('2024-06-01'),
          status,
        } as const satisfies DailyRecord;

        const view = DailyRecordLoggingMapper.toView(resultData);

        expect(view).toEqual({
          date: '2024-06-01',
          status: expectedStatus,
        });
      },
    );
  });
});
