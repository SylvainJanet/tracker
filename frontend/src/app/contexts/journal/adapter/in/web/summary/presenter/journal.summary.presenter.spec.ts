import { describe, expect, it } from 'vitest';

import { JournalSummaryPresenter } from './journal.summary.presenter';
import type { GetDefaultDailyRecordDateUseCase } from '../../../../../application/port/in/get-default-daily-record-date.use-case';
import { calendarDate } from '../../../../../domain/calendar-date';

describe('JournalSummaryPresenter', () => {
  const date = calendarDate('2026-08-27');

  it('starts with default date selected', () => {
    const presenter = createPresenter();

    expect(presenter.model().selectedDate).toBe(date);
    expect(presenter.model().state).toEqual({ kind: 'view', view: { date } });
  });

  it('loads and presents the selected daily record', async () => {
    const presenter = createPresenter();

    await presenter.selectDate(date);

    expect(presenter.model().state).toEqual({
      kind: 'view',
      view: {
        date: '2026-08-27',
      },
    });
  });

  it('presents an invalid date without invoking the use case', async () => {
    const presenter = createPresenter();

    await presenter.selectDate('not-a-date');

    expect(presenter.model().selectedDate).toBe('not-a-date');
    expect(presenter.model().state).toEqual({
      kind: 'problem',
      problem: 'invalid-date',
      title: 'Invalid date',
      message: 'Select a valid calendar date.',
    });
  });

  function createPresenter(): JournalSummaryPresenter {
    const getDailyRecord: GetDefaultDailyRecordDateUseCase = {
      execute: () => {
        return {
          resultData: date,
        };
      },
    };

    return new JournalSummaryPresenter(getDailyRecord);
  }
});
