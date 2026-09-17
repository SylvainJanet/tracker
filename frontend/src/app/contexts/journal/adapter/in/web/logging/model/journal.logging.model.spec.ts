import { describe, expect, it } from 'vitest';

import { JournalLoggingModel } from './journal.logging.model';

import { calendarDate } from '../../../../../domain/calendar-date';
import { dailyRecord } from '../../../../../domain/daily-record';

describe('DailyRecordLoggingModel', () => {
  it('selects a different date and resets presentation state', () => {
    const model = new JournalLoggingModel('2026-08-27', 1234, {
      kind: 'problem',
      problem: 'failure',
      title: 'Unable to create daily record',
      message: 'The daily record could not be created.',
    });

    const selectedModel = model.selectDate('2026-08-28');

    expect(selectedModel.selectedDate).toBe('2026-08-28');
    expect(selectedModel.selectedWeight).toBe(1234);
    expect(selectedModel.state).toEqual({ kind: 'idle' });
  });

  it('selects a different weight and resets presentation state', () => {
    const model = new JournalLoggingModel('2026-08-27', 1234, {
      kind: 'problem',
      problem: 'failure',
      title: 'Unable to create daily record',
      message: 'The daily record could not be created.',
    });

    const selectedModel = model.selectWeight(5678);

    expect(selectedModel.selectedDate).toBe('2026-08-27');
    expect(selectedModel.selectedWeight).toBe(5678);
    expect(selectedModel.state).toEqual({ kind: 'idle' });
  });

  it('does not change the selected date while creation is loading', () => {
    const loadingModel = JournalLoggingModel.initial('2026-08-27')
      .selectWeight(1234)
      .requestCreation().model;

    expect(loadingModel.selectDate('2026-08-28')).toBe(loadingModel);
  });

  it('does not change the selected weight while creation is loading', () => {
    const loadingModel = JournalLoggingModel.initial('2026-08-27')
      .selectWeight(1234)
      .requestCreation().model;

    expect(loadingModel.selectWeight(5678)).toBe(loadingModel);
  });

  it('starts creation for a valid selected date', () => {
    const model = JournalLoggingModel.initial('2026-08-27');
    const selected = model.selectWeight(1234);

    const transition = selected.requestCreation();

    expect(transition.effect).toEqual({
      kind: 'create',
      command: dailyRecord('2026-08-27', 1234),
    });

    expect(transition.model.selectedDate).toBe('2026-08-27');
    expect(transition.model.selectedWeight).toBe(1234);
    expect(transition.model.state).toEqual({ kind: 'loading' });
  });

  it('presents an invalid selected date without requesting creation', () => {
    const model = JournalLoggingModel.initial('not-a-date');

    const transition = model.requestCreation();

    expect(transition.effect).toEqual({ kind: 'none' });
    expect(transition.model.state).toEqual({
      kind: 'problem',
      problem: 'invalid-date',
      title: 'Invalid date',
      message: 'Select a valid calendar date.',
    });
  });
  it('presents an invalid weight without requesting creation', () => {
    const model = JournalLoggingModel.initial('2026-08-27').selectWeight(-1234);

    const transition = model.requestCreation();

    expect(transition.effect).toEqual({ kind: 'none' });
    expect(transition.model.state).toEqual({
      kind: 'problem',
      problem: 'invalid-weight',
      title: 'Invalid weight',
      message: 'Select a valid weight.',
    });
  });

  it('does not request another creation while creation is loading', () => {
    const loadingModel = JournalLoggingModel.initial('2026-08-27')
      .selectWeight(1234)
      .requestCreation().model;

    const transition = loadingModel.requestCreation();

    expect(transition.model).toBe(loadingModel);
    expect(transition.effect).toEqual({ kind: 'none' });
  });

  it('presents the created record when creation succeeds', () => {
    const loadingModel = JournalLoggingModel.initial('2026-08-27')
      .selectWeight(1234)
      .requestCreation().model;

    const model = loadingModel.creationSucceeded({
      date: '2026-08-27',
      weight: 1234,
    });

    expect(model.state).toEqual({
      kind: 'view',
      view: {
        date: '2026-08-27',
        weight: 1234,
      },
    });
  });

  it('ignores creation success when creation is not loading', () => {
    const model = JournalLoggingModel.initial('2026-08-27');

    const transitionedModel = model.creationSucceeded({
      date: '2026-08-27',
      weight: 1234,
    });

    expect(transitionedModel).toBe(model);
  });

  it('presents an existing-record problem when creation finds a conflict', () => {
    const date = calendarDate('2026-08-27');
    const loadingModel = JournalLoggingModel.initial(date)
      .selectWeight(1234)
      .requestCreation().model;

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
    const model = JournalLoggingModel.initial(date);

    expect(model.recordAlreadyExists(date)).toBe(model);
  });

  it('presents a creation failure while creation is loading', () => {
    const loadingModel = JournalLoggingModel.initial('2026-08-27')
      .selectWeight(1234)
      .requestCreation().model;

    const model = loadingModel.creationFailed();

    expect(model.state).toEqual({
      kind: 'problem',
      problem: 'failure',
      title: 'Unable to create daily record',
      message: 'The daily record could not be created.',
    });
  });

  it('ignores a creation failure when creation is not loading', () => {
    const model = JournalLoggingModel.initial('2026-08-27');

    expect(model.creationFailed()).toBe(model);
  });
});
