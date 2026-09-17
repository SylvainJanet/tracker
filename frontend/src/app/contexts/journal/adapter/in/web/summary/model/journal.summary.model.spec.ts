import { describe, expect, it } from 'vitest';

import { JournalSummaryModel } from './journal.summary.model';

describe('DailyRecordSummaryModel', () => {
  it('presents an invalid date without requesting loading', () => {
    const model = JournalSummaryModel.initial('2026-08-27');

    const transition = model.selectDate('not-a-date');

    expect(transition.effect).toEqual({ kind: 'none' });
    expect(transition.model.selectedDate).toBe('not-a-date');
    expect(transition.model.state).toEqual({
      kind: 'problem',
      problem: 'invalid-date',
      title: 'Invalid date',
      message: 'Select a valid calendar date.',
    });
  });

  it('presents a found daily record', () => {
    const loadingModel = JournalSummaryModel.initial('2026-08-27').selectDate('2026-08-27').model;

    const model = loadingModel.recordFound({
      date: '2026-08-27',
    });

    expect(model.state).toEqual({
      kind: 'view',
      view: {
        date: '2026-08-27',
      },
    });
  });
});
