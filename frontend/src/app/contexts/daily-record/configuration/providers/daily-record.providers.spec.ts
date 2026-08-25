import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

import { DailyRecordLoggingPage } from '../../adapter/in/web/logging/page/daily-record.logging.page';
import { DailyRecordSummaryPage } from '../../adapter/in/web/summary/page/daily-record.summary.page';
import { type CreateDailyRecordUseCase } from '../../application/port/in/create-daily-record.use-case';
import { type GetDailyRecordUseCase } from '../../application/port/in/get-daily-record.use-case';
import { calendarDate } from '../../domain/calendar-date';
import { type DailyRecord } from '../../domain/daily-record';
import {
  CREATE_DAILY_RECORD_USE_CASE,
  GET_DAILY_RECORD_USE_CASE,
  GET_DEFAULT_DAILY_RECORD_DATE_USE_CASE,
  provideDailyRecordContext,
  provideDailyRecordLoggingPresenter,
  provideDailyRecordSummaryPresenter,
} from './daily-record.providers';
import type { GetDefaultDailyRecordDateUseCase } from '../../application/port/in/get-default-daily-record-date.use-case';

const date = calendarDate('2026-08-27');

const resultData: DailyRecord = {
  date,
  status: 'IN_PROGRESS',
};

describe('provideDailyRecordContext', () => {
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideDailyRecordContext()],
    });

    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('wires the create use case through the HTTP gateway', async () => {
    const createDailyRecord = TestBed.inject(CREATE_DAILY_RECORD_USE_CASE);

    const resultPromise = createDailyRecord.execute(date);
    const request = httpTestingController.expectOne('/api/daily-records');

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ date });

    request.flush(resultData);

    await expect(resultPromise).resolves.toEqual({
      kind: 'created',
      resultData,
    });
  });

  it('wires the get use case through the HTTP gateway', async () => {
    const getDailyRecord = TestBed.inject(GET_DAILY_RECORD_USE_CASE);

    const resultPromise = getDailyRecord.execute(date);
    const request = httpTestingController.expectOne('/api/daily-records/2026-08-27');

    expect(request.request.method).toBe('GET');

    request.flush(resultData);

    await expect(resultPromise).resolves.toEqual({
      kind: 'found',
      resultData,
    });
  });

  it('wires the default daily-record date use case through the browser provider', () => {
    vi.useFakeTimers();

    try {
      vi.setSystemTime(new Date(2026, 7, 27, 12));

      const getDefaultDailyRecordDate = TestBed.inject(GET_DEFAULT_DAILY_RECORD_DATE_USE_CASE);

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
      resultData,
    }),
  };

  const getDailyRecord: GetDailyRecordUseCase = {
    execute: async () => ({
      kind: 'found',
      resultData,
    }),
  };

  const getDefaultDailyRecordDate: GetDefaultDailyRecordDateUseCase = {
    execute: () => {
      return { resultData: date };
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DailyRecordLoggingPage, DailyRecordSummaryPage],
      providers: [
        provideRouter([]),
        {
          provide: CREATE_DAILY_RECORD_USE_CASE,
          useValue: createDailyRecord,
        },
        {
          provide: GET_DAILY_RECORD_USE_CASE,
          useValue: getDailyRecord,
        },
        {
          provide: GET_DEFAULT_DAILY_RECORD_DATE_USE_CASE,
          useValue: getDefaultDailyRecordDate,
        },
        provideDailyRecordLoggingPresenter(),
        provideDailyRecordSummaryPresenter(),
      ],
    }).compileComponents();
  });

  it('creates a logging presenter for each page instance', () => {
    const firstPage = TestBed.createComponent(DailyRecordLoggingPage);
    const secondPage = TestBed.createComponent(DailyRecordLoggingPage);

    expect(firstPage.componentInstance.presenter).not.toBe(secondPage.componentInstance.presenter);
  });

  it('creates a summary presenter for each page instance', () => {
    const firstPage = TestBed.createComponent(DailyRecordSummaryPage);
    const secondPage = TestBed.createComponent(DailyRecordSummaryPage);

    expect(firstPage.componentInstance.presenter).not.toBe(secondPage.componentInstance.presenter);
  });
});
