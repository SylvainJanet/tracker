import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

import { JournalLoggingPage } from '../../adapter/in/web/logging/page/journal.logging.page';
import { JournalSummaryPage } from '../../adapter/in/web/summary/page/journal.summary.page';
import { type CreateDailyRecordUseCase } from '../../application/port/in/create-daily-record.use-case';
import { calendarDate } from '../../domain/calendar-date';
import { dailyRecord } from '../../domain/daily-record';
import type { GetDefaultDailyRecordDateUseCase } from '../../application/port/in/get-default-daily-record-date.use-case';
import {
  JOURNAL_CREATE_DAILY_RECORD_USE_CASE,
  JOURNAL_GET_DEFAULT_DAILY_RECORD_DATE_USE_CASE,
  provideJournalContext,
  provideJournalLoggingPresenter,
  provideJournalSummaryPresenter,
} from './journal.providers';

const date = calendarDate('2026-08-27');
const command = dailyRecord(date, 1234);

describe('provideDailyRecordContext', () => {
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideJournalContext()],
    });

    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('wires the create use case through the HTTP gateway', async () => {
    const createDailyRecord = TestBed.inject(JOURNAL_CREATE_DAILY_RECORD_USE_CASE);

    const resultPromise = createDailyRecord.execute(command);
    const request = httpTestingController.expectOne('/api/daily-records');

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ date: '2026-08-27', weight: 1234 });

    request.flush(command);

    await expect(resultPromise).resolves.toEqual({
      kind: 'created',
      resultData: command,
    });
  });

  it('wires the default daily-record date use case through the browser provider', () => {
    vi.useFakeTimers();

    try {
      vi.setSystemTime(new Date(2026, 7, 27, 12));

      const getDefaultDailyRecordDate = TestBed.inject(
        JOURNAL_GET_DEFAULT_DAILY_RECORD_DATE_USE_CASE,
      );

      expect(getDefaultDailyRecordDate.execute()).toEqual({
        resultData: date,
      });
    } finally {
      vi.useRealTimers();
    }
  });
});

describe('daily-record presenter providers', () => {
  const createDailyRecord: CreateDailyRecordUseCase = {
    execute: async () => ({
      kind: 'created',
      resultData: command,
    }),
  };

  const getDefaultDailyRecordDate: GetDefaultDailyRecordDateUseCase = {
    execute: () => {
      return { resultData: date };
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JournalLoggingPage, JournalSummaryPage],
      providers: [
        provideRouter([]),
        {
          provide: JOURNAL_CREATE_DAILY_RECORD_USE_CASE,
          useValue: createDailyRecord,
        },
        {
          provide: JOURNAL_GET_DEFAULT_DAILY_RECORD_DATE_USE_CASE,
          useValue: getDefaultDailyRecordDate,
        },
        provideJournalLoggingPresenter(),
        provideJournalSummaryPresenter(),
      ],
    }).compileComponents();
  });

  it('creates a logging presenter for each page instance', () => {
    const firstPage = TestBed.createComponent(JournalLoggingPage);
    const secondPage = TestBed.createComponent(JournalLoggingPage);

    expect(firstPage.componentInstance.presenter).not.toBe(secondPage.componentInstance.presenter);
  });

  it('creates a summary presenter for each page instance', () => {
    const firstPage = TestBed.createComponent(JournalSummaryPage);
    const secondPage = TestBed.createComponent(JournalSummaryPage);

    expect(firstPage.componentInstance.presenter).not.toBe(secondPage.componentInstance.presenter);
  });
});
