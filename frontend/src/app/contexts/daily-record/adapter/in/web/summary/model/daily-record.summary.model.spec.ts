import { describe, expect, it } from 'vitest';

import { calendarDate } from '../../../../../domain/calendar-date';
import { DailyRecordSummaryModel } from './daily-record.summary.model';

describe('DailyRecordSummaryModel', () => {
  it('requests loading the selected date', () => {
    const model = DailyRecordSummaryModel.initial('2026-08-27');

    const transition = model.selectDate('2026-08-27');

    expect(transition.effect).toEqual({
      kind: 'load',
      query: '2026-08-27',
    });
    expect(transition.model.selectedDate).toBe('2026-08-27');
    expect(transition.model.state).toEqual({ kind: 'loading' });
  });

  it('presents an invalid date without requesting loading', () => {
    const model = DailyRecordSummaryModel.initial('2026-08-27');

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

  it('does not request another load while loading', () => {
    const loadingModel =
      DailyRecordSummaryModel.initial('2026-08-27').selectDate('2026-08-27').model;

    const transition = loadingModel.selectDate('2026-08-28');

    expect(transition.model).toBe(loadingModel);
    expect(transition.effect).toEqual({ kind: 'none' });
  });

  it('presents a found daily record', () => {
    const loadingModel =
      DailyRecordSummaryModel.initial('2026-08-27').selectDate('2026-08-27').model;

    const model = loadingModel.recordFound({
      date: '2026-08-27',
      status: 'In progress',
    });

    expect(model.state).toEqual({
      kind: 'view',
      view: {
        date: '2026-08-27',
        status: 'In progress',
      },
    });
  });

  it('ignores a found record when not loading', () => {
    const model = DailyRecordSummaryModel.initial('2026-08-27');

    expect(
      model.recordFound({
        date: '2026-08-27',
        status: 'In progress',
      }),
    ).toBe(model);
  });

  it('presents a missing daily record', () => {
    const query = calendarDate('2026-08-27');
    const loadingModel = DailyRecordSummaryModel.initial(query).selectDate(query).model;

    const model = loadingModel.recordNotFound(query);

    expect(model.state).toEqual({
      kind: 'problem',
      problem: 'not-found',
      title: 'Daily record not found',
      message: 'No daily record exists for 2026-08-27.',
    });
  });

  it('ignores a missing record when not loading', () => {
    const query = calendarDate('2026-08-27');
    const model = DailyRecordSummaryModel.initial(query);

    expect(model.recordNotFound(query)).toBe(model);
  });

  it('presents a loading failure', () => {
    const loadingModel =
      DailyRecordSummaryModel.initial('2026-08-27').selectDate('2026-08-27').model;

    const model = loadingModel.recordLoadingFailed();

    expect(model.state).toEqual({
      kind: 'problem',
      problem: 'failure',
      title: 'Unable to load daily record',
      message: 'The daily record could not be loaded.',
    });
  });

  it('ignores a loading failure when not loading', () => {
    const model = DailyRecordSummaryModel.initial('2026-08-27');

    expect(model.recordLoadingFailed()).toBe(model);
  });
});
