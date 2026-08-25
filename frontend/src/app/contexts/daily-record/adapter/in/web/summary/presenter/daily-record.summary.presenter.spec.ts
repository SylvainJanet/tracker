import { describe, expect, it } from 'vitest';

import { type GetDailyRecordUseCase } from '../../../../../application/port/in/get-daily-record.use-case';
import { calendarDate, type CalendarDate } from '../../../../../domain/calendar-date';
import { type DailyRecord } from '../../../../../domain/daily-record';
import { DailyRecordSummaryPresenter } from './daily-record.summary.presenter';
import type { GetDefaultDailyRecordDateUseCase } from '../../../../../application/port/in/get-default-daily-record-date.use-case';

describe('DailyRecordSummaryPresenter', () => {
  const date = calendarDate('2026-08-27');

  const resultData: DailyRecord = {
    date,
    status: 'IN_PROGRESS',
  };

  it('starts with default date selected', () => {
    const presenter = createPresenter();

    expect(presenter.model().selectedDate).toBe(date);
    expect(presenter.model().loading).toBe(false);
    expect(presenter.model().state).toEqual({ kind: 'idle' });
  });

  it('loads and presents the selected daily record', async () => {
    let receivedQuery: CalendarDate | null = null;

    const presenter = createPresenter(async (query) => {
      receivedQuery = query;

      return {
        kind: 'found',
        resultData,
      };
    });

    await presenter.selectDate(date);

    expect(receivedQuery).toBe(date);
    expect(presenter.model().state).toEqual({
      kind: 'view',
      view: {
        date: '2026-08-27',
        status: 'In progress',
      },
    });
  });

  it('presents a missing daily record', async () => {
    const presenter = createPresenter(async () => ({
      kind: 'not-found',
    }));

    await presenter.selectDate(date);

    expect(presenter.model().state).toEqual({
      kind: 'problem',
      problem: 'not-found',
      title: 'Daily record not found',
      message: 'No daily record exists for 2026-08-27.',
    });
  });

  it('presents an invalid date without invoking the use case', async () => {
    let called = false;

    const presenter = createPresenter(async () => {
      called = true;

      return {
        kind: 'not-found',
      };
    });

    await presenter.selectDate('not-a-date');

    expect(called).toBe(false);
    expect(presenter.model().selectedDate).toBe('not-a-date');
    expect(presenter.model().state).toEqual({
      kind: 'problem',
      problem: 'invalid-date',
      title: 'Invalid date',
      message: 'Select a valid calendar date.',
    });
  });

  it('presents unexpected failures generically', async () => {
    const presenter = createPresenter(async () => {
      throw new Error('network details');
    });

    await presenter.selectDate(date);

    expect(presenter.model().state).toEqual({
      kind: 'problem',
      problem: 'failure',
      title: 'Unable to load daily record',
      message: 'The daily record could not be loaded.',
    });
  });

  function createPresenter(
    execute: GetDailyRecordUseCase['execute'] = async () => ({
      kind: 'found',
      resultData,
    }),
  ): DailyRecordSummaryPresenter {
    const getDailyRecord: GetDailyRecordUseCase = {
      execute,
    };

    const getDefaultDailyRecordDate: GetDefaultDailyRecordDateUseCase = {
      execute: () => {
        return {
          resultData: date,
        };
      },
    };

    return new DailyRecordSummaryPresenter(getDailyRecord, getDefaultDailyRecordDate);
  }
});
