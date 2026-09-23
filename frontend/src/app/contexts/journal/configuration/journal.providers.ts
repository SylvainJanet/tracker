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
import type { WeightMeasurementStore } from '../application/port/out/weight-measurement.store';
import { WeightMeasurementGateway } from '../adapter/out/http/weight-measurement.gateway';
import { GetDefaultJournalDateService } from '../application/service/get-default-journal-date.service';
import { LogWeightMeasurementService } from '../application/service/log-weight-measurement.service';
import type { GetWeightMeasurementByDateUseCase } from '../application/port/in/get-weight-measurement-by-date.use-case';
import { GetWeightMeasurementByDateService } from '../application/service/get-weight-measurement-by-date.service';

export const GET_DEFAULT_JOURNAL_DATE_USE_CASE = new InjectionToken<GetDefaultJournalDateUseCase>(
  'GetDefaultJournalDateUseCase',
);
export const LOG_WEIGHT_MEASUREMENT_USE_CASE = new InjectionToken<LogWeightMeasurementUseCase>(
  'LogWeightMeasurementUseCase',
);
export const GET_WEIGHT_MEASUREMENT_BY_DATE_USE_CASE =
  new InjectionToken<GetWeightMeasurementByDateUseCase>('GetWeightMeasurementByDateUseCase');

const WEIGHT_MEASUREMENT_STORE = new InjectionToken<WeightMeasurementStore>(
  'WeightMeasurementStore',
);
const TODAY_PROVIDER = new InjectionToken<TodayProvider>('TodayProvider');

export function provideJournalContext(): EnvironmentProviders {
  return makeEnvironmentProviders([
    {
      provide: WEIGHT_MEASUREMENT_STORE,
      useFactory: (httpClient: HttpClient): WeightMeasurementStore =>
        new WeightMeasurementGateway(httpClient),
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
      useFactory: (store: WeightMeasurementStore): LogWeightMeasurementUseCase =>
        new LogWeightMeasurementService(store),
      deps: [WEIGHT_MEASUREMENT_STORE],
    },
    {
      provide: GET_WEIGHT_MEASUREMENT_BY_DATE_USE_CASE,
      useFactory: (store: WeightMeasurementStore): GetWeightMeasurementByDateUseCase =>
        new GetWeightMeasurementByDateService(store),
      deps: [WEIGHT_MEASUREMENT_STORE],
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
        getWeightMeasurementByDateUseCase: GetWeightMeasurementByDateUseCase,
      ): JournalLoggingPresenterFactory => {
        return () =>
          new JournalLoggingPresenter(
            logWeightMeasurementUseCase,
            getWeightMeasurementByDateUseCase,
            getDefaultJournalDateUseCase,
          );
      },
      deps: [
        GET_DEFAULT_JOURNAL_DATE_USE_CASE,
        LOG_WEIGHT_MEASUREMENT_USE_CASE,
        GET_WEIGHT_MEASUREMENT_BY_DATE_USE_CASE,
      ],
    },
  ]);
}
