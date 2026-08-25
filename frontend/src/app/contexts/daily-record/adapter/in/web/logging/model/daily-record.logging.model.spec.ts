import { describe, expect, it } from 'vitest';

import { DailyRecordLoggingModel } from './daily-record.logging.model';

import { calendarDate } from '../../../../../domain/calendar-date';

describe('DailyRecordLoggingModel', () => {
  it('selects a different date and resets presentation state', () => {
    const model = new DailyRecordLoggingModel('2026-08-27', {
      kind: 'problem',
      problem: 'failure',
      title: 'Unable to create daily record',
      message: 'The daily record could not be created.',
    });

    const selectedModel = model.selectDate('2026-08-28');

    expect(selectedModel.selectedDate).toBe('2026-08-28');
    expect(selectedModel.state).toEqual({ kind: 'idle' });
  });

  it('does not change the selected date while creation is loading', () => {
    const loadingModel = DailyRecordLoggingModel.initial('2026-08-27').requestCreation().model;

    expect(loadingModel.selectDate('2026-08-28')).toBe(loadingModel);
  });

  it('starts creation for a valid selected date', () => {
    const model = DailyRecordLoggingModel.initial('2026-08-27');

    const transition = model.requestCreation();

    expect(transition.effect).toEqual({
      kind: 'create',
      command: '2026-08-27',
    });

    expect(transition.model.selectedDate).toBe('2026-08-27');
    expect(transition.model.state).toEqual({ kind: 'loading' });
  });

  it('presents an invalid selected date without requesting creation', () => {
    const model = DailyRecordLoggingModel.initial('not-a-date');

    const transition = model.requestCreation();

    expect(transition.effect).toEqual({ kind: 'none' });
    expect(transition.model.state).toEqual({
      kind: 'problem',
      problem: 'invalid-date',
      title: 'Invalid date',
      message: 'Select a valid calendar date.',
    });
  });

  it('does not request another creation while creation is loading', () => {
    const loadingModel = DailyRecordLoggingModel.initial('2026-08-27').requestCreation().model;

    const transition = loadingModel.requestCreation();

    expect(transition.model).toBe(loadingModel);
    expect(transition.effect).toEqual({ kind: 'none' });
  });

  it('presents the created record when creation succeeds', () => {
    const loadingModel = DailyRecordLoggingModel.initial('2026-08-27').requestCreation().model;

    const model = loadingModel.creationSucceeded({
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

  it('ignores creation success when creation is not loading', () => {
    const model = DailyRecordLoggingModel.initial('2026-08-27');

    const transitionedModel = model.creationSucceeded({
      date: '2026-08-27',
      status: 'In progress',
    });

    expect(transitionedModel).toBe(model);
  });

  it('presents an existing-record problem when creation finds a conflict', () => {
    const date = calendarDate('2026-08-27');
    const loadingModel = DailyRecordLoggingModel.initial(date).requestCreation().model;

    const model = loadingModel.recordAlreadyExists(date);

    expect(model.state).toEqual({
      kind: 'problem',
      problem: 'already-exists',
      title: 'Daily record already exists',
      message: 'A daily record already exists for 2026-08-27.',
    });
  });

  it('ignores an existing-record result when creation is not loading', () => {
    const date = calendarDate('2026-08-27');
    const model = DailyRecordLoggingModel.initial(date);

    expect(model.recordAlreadyExists(date)).toBe(model);
  });

  it('presents a creation failure while creation is loading', () => {
    const loadingModel = DailyRecordLoggingModel.initial('2026-08-27').requestCreation().model;

    const model = loadingModel.creationFailed();

    expect(model.state).toEqual({
      kind: 'problem',
      problem: 'failure',
      title: 'Unable to create daily record',
      message: 'The daily record could not be created.',
    });
  });

  it('ignores a creation failure when creation is not loading', () => {
    const model = DailyRecordLoggingModel.initial('2026-08-27');

    expect(model.creationFailed()).toBe(model);
  });
});
