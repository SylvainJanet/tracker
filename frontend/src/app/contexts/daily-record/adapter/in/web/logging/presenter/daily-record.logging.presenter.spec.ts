import { describe, expect, it, vi } from 'vitest';

import { type CreateDailyRecordUseCase } from '../../../../../application/port/in/create-daily-record.use-case';
import { calendarDate } from '../../../../../domain/calendar-date';
import { type DailyRecord } from '../../../../../domain/daily-record';
import { DailyRecordLoggingPresenter } from './daily-record.logging.presenter';
import type { GetDefaultDailyRecordDateUseCase } from '../../../../../application/port/in/get-default-daily-record-date.use-case';

describe('DailyRecordLoggingPresenter', () => {
  const date = calendarDate('2026-08-27');

  const resultData: DailyRecord = {
    date,
    status: 'IN_PROGRESS',
  };

  it('starts with default date selected', () => {
    const presenter = createPresenter();

    expect(presenter.model().selectedDate).toBe(date);
    expect(presenter.model().loading).toBe(false);
    expect(presenter.model().canCreate).toBe(true);
    expect(presenter.model().state).toEqual({ kind: 'idle' });
  });

  it('creates and presents a daily record', async () => {
    const execute = vi.fn<CreateDailyRecordUseCase['execute']>(async () => ({
      kind: 'created',
      resultData,
    }));
    const presenter = createPresenter(execute);

    await presenter.create();

    expect(execute).toHaveBeenCalledOnce();
    expect(execute).toHaveBeenCalledWith(date);
    expect(presenter.model().state).toEqual({
      kind: 'view',
      view: {
        date: '2026-08-27',
        status: 'In progress',
      },
    });
  });

  it('presents an existing-record conflict', async () => {
    const presenter = createPresenter(async () => ({
      kind: 'already-exists',
    }));

    await presenter.create();

    expect(presenter.model().state).toEqual({
      kind: 'problem',
      problem: 'already-exists',
      title: 'Daily record already exists',
      message: 'A daily record already exists for 2026-08-27.',
    });
  });

  it('presents an invalid date without invoking the use case', async () => {
    let called = false;

    const presenter = createPresenter(async () => {
      called = true;

      return {
        kind: 'already-exists',
      };
    });

    presenter.selectDate('not-a-date');
    await presenter.create();

    expect(called).toBe(false);
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

    await presenter.create();

    expect(presenter.model().state).toEqual({
      kind: 'problem',
      problem: 'failure',
      title: 'Unable to create daily record',
      message: 'The daily record could not be created.',
    });
  });

  function createPresenter(
    execute: CreateDailyRecordUseCase['execute'] = async () => ({
      kind: 'created',
      resultData,
    }),
  ): DailyRecordLoggingPresenter {
    const createDailyRecord: CreateDailyRecordUseCase = {
      execute,
    };

    const getDefaultDailyRecordDate: GetDefaultDailyRecordDateUseCase = {
      execute: () => {
        return {
          resultData: date,
        };
      },
    };

    return new DailyRecordLoggingPresenter(createDailyRecord, getDefaultDailyRecordDate);
  }
});
