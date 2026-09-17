import { InjectionToken, makeEnvironmentProviders, type EnvironmentProviders } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import type { CreateDailyRecordUseCase } from '../../application/port/in/create-daily-record.use-case';
import type { DailyRecordGateway } from '../../application/port/out/daily-record.gateway';
import type { TodayProvider } from '../../application/port/out/today.provider';
import { HttpDailyRecordGateway } from '../../adapter/out/http/http-daily-record.gateway';
import { BrowserTodayProvider } from '../../adapter/out/time/browser-today.provider';
import { CreateDailyRecordService } from '../../application/service/create-daily-record.service';
import {
  JOURNAL_LOGGING_PRESENTER_FACTORY,
  JournalLoggingPresenter,
  type JournalLoggingPresenterFactory,
} from '../../adapter/in/web/logging/presenter/journal.logging.presenter';
import type { GetDefaultDailyRecordDateUseCase } from '../../application/port/in/get-default-daily-record-date.use-case';
import { GetDefaultDailyRecordDateService } from '../../application/service/get-default-daily-record-date.service';
import {
  JOURNAL_SUMMARY_PRESENTER_FACTORY,
  JournalSummaryPresenter,
  type JournalSummaryPresenterFactory,
} from '../../adapter/in/web/summary/presenter/journal.summary.presenter';

export const JOURNAL_CREATE_DAILY_RECORD_USE_CASE = new InjectionToken<CreateDailyRecordUseCase>(
  'JournalCreateDailyRecordUseCase',
);
export const JOURNAL_GET_DEFAULT_DAILY_RECORD_DATE_USE_CASE =
  new InjectionToken<GetDefaultDailyRecordDateUseCase>('JournalGetDefaultDailyRecordDateUseCase');

const DAILY_RECORD_GATEWAY = new InjectionToken<DailyRecordGateway>('DailyRecordGateway');
const TODAY_PROVIDER = new InjectionToken<TodayProvider>('TodayProvider');

export function provideJournalContext(): EnvironmentProviders {
  return makeEnvironmentProviders([
    {
      provide: DAILY_RECORD_GATEWAY,
      useFactory: (httpClient: HttpClient): DailyRecordGateway =>
        new HttpDailyRecordGateway(httpClient),
      deps: [HttpClient],
    },
    {
      provide: TODAY_PROVIDER,
      useFactory: (): TodayProvider => new BrowserTodayProvider(),
      deps: [],
    },
    {
      provide: JOURNAL_CREATE_DAILY_RECORD_USE_CASE,
      useFactory: (gateway: DailyRecordGateway): CreateDailyRecordUseCase =>
        new CreateDailyRecordService(gateway),
      deps: [DAILY_RECORD_GATEWAY],
    },
    {
      provide: JOURNAL_GET_DEFAULT_DAILY_RECORD_DATE_USE_CASE,
      useFactory: (provider: TodayProvider): GetDefaultDailyRecordDateUseCase =>
        new GetDefaultDailyRecordDateService(provider),
      deps: [TODAY_PROVIDER],
    },
  ]);
}

export function provideJournalLoggingPresenter(): EnvironmentProviders {
  return makeEnvironmentProviders([
    {
      provide: JOURNAL_LOGGING_PRESENTER_FACTORY,
      useFactory: (
        createDailyRecord: CreateDailyRecordUseCase,
        getDefaultDailyRecordDate: GetDefaultDailyRecordDateUseCase,
      ): JournalLoggingPresenterFactory => {
        return () => new JournalLoggingPresenter(createDailyRecord, getDefaultDailyRecordDate);
      },
      deps: [JOURNAL_CREATE_DAILY_RECORD_USE_CASE, JOURNAL_GET_DEFAULT_DAILY_RECORD_DATE_USE_CASE],
    },
  ]);
}

export function provideJournalSummaryPresenter(): EnvironmentProviders {
  return makeEnvironmentProviders([
    {
      provide: JOURNAL_SUMMARY_PRESENTER_FACTORY,
      useFactory: (
        getDefaultDailyRecordDate: GetDefaultDailyRecordDateUseCase,
      ): JournalSummaryPresenterFactory => {
        return () => new JournalSummaryPresenter(getDefaultDailyRecordDate);
      },
      deps: [JOURNAL_GET_DEFAULT_DAILY_RECORD_DATE_USE_CASE],
    },
  ]);
}
