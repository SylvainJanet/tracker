import { type EnvironmentProviders, InjectionToken, makeEnvironmentProviders } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import type { TodayProvider } from '../application/port/out/today.provider';
import { BrowserTodayProvider } from '../adapter/out/time/browser-today.provider';
import {
  JOURNAL_LOGGING_PRESENTER_FACTORY,
  JournalLoggingPresenter,
  type JournalLoggingPresenterFactory,
} from '../adapter/in/web/logging/presenter/journal.logging.presenter';
import type { GetDefaultJournalDateUseCase } from '../application/port/in/get-default-journal-date.use-case';
import type { LogWeightMeasurementUseCase } from '../application/port/in/log-weight-measurement.use-case';
import type { LogWeightMeasurementStore } from '../application/port/out/log-weight-measurement.store';
import { LogWeightMeasurementGateway } from '../adapter/out/http/log-wright-measurement.gateway';
import { GetDefaultJournalDateService } from '../application/service/get-default-journal-date.service';
import { LogWeightMeasurementService } from '../application/service/log-weight-measurement.service';

export const GET_DEFAULT_JOURNAL_DATE_USE_CASE = new InjectionToken<GetDefaultJournalDateUseCase>(
  'GetDefaultJournalDateUseCase',
);
export const LOG_WEIGHT_MEASUREMENT_USE_CASE = new InjectionToken<LogWeightMeasurementUseCase>(
  'LogWeightMeasurementUseCase',
);

const LOG_WEIGHT_MEASUREMENT_STORE = new InjectionToken<LogWeightMeasurementStore>(
  'LogWeightMeasurementStore',
);
const TODAY_PROVIDER = new InjectionToken<TodayProvider>('TodayProvider');

export function provideJournalContext(): EnvironmentProviders {
  return makeEnvironmentProviders([
    {
      provide: LOG_WEIGHT_MEASUREMENT_STORE,
      useFactory: (httpClient: HttpClient): LogWeightMeasurementStore =>
        new LogWeightMeasurementGateway(httpClient),
      deps: [HttpClient],
    },
    {
      provide: TODAY_PROVIDER,
      useFactory: (): TodayProvider => new BrowserTodayProvider(),
      deps: [],
    },
    {
      provide: GET_DEFAULT_JOURNAL_DATE_USE_CASE,
      useFactory: (provider: TodayProvider): GetDefaultJournalDateUseCase =>
        new GetDefaultJournalDateService(provider),
      deps: [TODAY_PROVIDER],
    },
    {
      provide: LOG_WEIGHT_MEASUREMENT_USE_CASE,
      useFactory: (store: LogWeightMeasurementStore): LogWeightMeasurementUseCase =>
        new LogWeightMeasurementService(store),
      deps: [LOG_WEIGHT_MEASUREMENT_STORE],
    },
  ]);
}

export function provideJournalLoggingPresenter(): EnvironmentProviders {
  return makeEnvironmentProviders([
    {
      provide: JOURNAL_LOGGING_PRESENTER_FACTORY,
      useFactory: (
        getDefaultJournalDateUseCase: GetDefaultJournalDateUseCase,
        logWeightMeasurementUseCase: LogWeightMeasurementUseCase,
      ): JournalLoggingPresenterFactory => {
        return () =>
          new JournalLoggingPresenter(logWeightMeasurementUseCase, getDefaultJournalDateUseCase);
      },
      deps: [GET_DEFAULT_JOURNAL_DATE_USE_CASE, LOG_WEIGHT_MEASUREMENT_USE_CASE],
    },
  ]);
}
