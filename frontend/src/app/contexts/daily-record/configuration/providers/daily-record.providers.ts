import { type EnvironmentProviders, InjectionToken, makeEnvironmentProviders } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { type CreateDailyRecordUseCase } from '../../application/port/in/create-daily-record.use-case';
import { type GetDailyRecordUseCase } from '../../application/port/in/get-daily-record.use-case';
import { type DailyRecordGateway } from '../../application/port/out/daily-record.gateway';
import { CreateDailyRecordService } from '../../application/service/create-daily-record.service';
import { GetDailyRecordService } from '../../application/service/get-daily-record.service';
import { HttpDailyRecordGateway } from '../../adapter/out/http/http-daily-record.gateway';
import { BrowserTodayProvider } from '../../adapter/out/time/browser-today.provider';
import { type TodayProvider } from '../../application/port/out/today.provider';
import {
  DAILY_RECORD_SUMMARY_PRESENTER_FACTORY,
  DailyRecordSummaryPresenter,
  type DailyRecordSummaryPresenterFactory,
} from '../../adapter/in/web/summary/presenter/daily-record.summary.presenter';
import {
  DAILY_RECORD_LOGGING_PRESENTER_FACTORY,
  type DailyRecordLoggingPresenterFactory,
  DailyRecordLoggingPresenter,
} from '../../adapter/in/web/logging/presenter/daily-record.logging.presenter';
import type { GetDefaultDailyRecordDateUseCase } from '../../application/port/in/get-default-daily-record-date.use-case';
import { GetDefaultDailyRecordDateService } from '../../application/service/get-default-daily-record-date.service';

export const GET_DAILY_RECORD_USE_CASE = new InjectionToken<GetDailyRecordUseCase>(
  'GetDailyRecordUseCase',
);

export const CREATE_DAILY_RECORD_USE_CASE = new InjectionToken<CreateDailyRecordUseCase>(
  'CreateDailyRecordUseCase',
);

export const GET_DEFAULT_DAILY_RECORD_DATE_USE_CASE =
  new InjectionToken<GetDefaultDailyRecordDateUseCase>('GetDefaultDailyRecordDateUseCase');

const DAILY_RECORD_GATEWAY = new InjectionToken<DailyRecordGateway>('DailyRecordGateway');
const TODAY_PROVIDER = new InjectionToken<TodayProvider>('TodayProvider');

export function provideDailyRecordContext(): EnvironmentProviders {
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
      provide: GET_DAILY_RECORD_USE_CASE,
      useFactory: (gateway: DailyRecordGateway): GetDailyRecordUseCase =>
        new GetDailyRecordService(gateway),
      deps: [DAILY_RECORD_GATEWAY],
    },
    {
      provide: CREATE_DAILY_RECORD_USE_CASE,
      useFactory: (gateway: DailyRecordGateway): CreateDailyRecordUseCase =>
        new CreateDailyRecordService(gateway),
      deps: [DAILY_RECORD_GATEWAY],
    },
    {
      provide: GET_DEFAULT_DAILY_RECORD_DATE_USE_CASE,
      useFactory: (provider: TodayProvider): GetDefaultDailyRecordDateUseCase =>
        new GetDefaultDailyRecordDateService(provider),
      deps: [TODAY_PROVIDER],
    },
  ]);
}

export function provideDailyRecordLoggingPresenter(): EnvironmentProviders {
  return makeEnvironmentProviders([
    {
      provide: DAILY_RECORD_LOGGING_PRESENTER_FACTORY,
      useFactory: (
        createDailyRecord: CreateDailyRecordUseCase,
        getDefaultDailyRecordDate: GetDefaultDailyRecordDateUseCase,
      ): DailyRecordLoggingPresenterFactory => {
        return () => new DailyRecordLoggingPresenter(createDailyRecord, getDefaultDailyRecordDate);
      },
      deps: [CREATE_DAILY_RECORD_USE_CASE, GET_DEFAULT_DAILY_RECORD_DATE_USE_CASE],
    },
  ]);
}

export function provideDailyRecordSummaryPresenter(): EnvironmentProviders {
  return makeEnvironmentProviders([
    {
      provide: DAILY_RECORD_SUMMARY_PRESENTER_FACTORY,
      useFactory: (
        getDailyRecord: GetDailyRecordUseCase,
        getDefaultDailyRecordDate: GetDefaultDailyRecordDateUseCase,
      ): DailyRecordSummaryPresenterFactory => {
        return () => new DailyRecordSummaryPresenter(getDailyRecord, getDefaultDailyRecordDate);
      },
      deps: [GET_DAILY_RECORD_USE_CASE, GET_DEFAULT_DAILY_RECORD_DATE_USE_CASE],
    },
  ]);
}
