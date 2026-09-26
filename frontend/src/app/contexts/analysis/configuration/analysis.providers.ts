import { HttpClient } from '@angular/common/http';
import { type EnvironmentProviders, InjectionToken, makeEnvironmentProviders } from '@angular/core';

import { HttpWeightAnalysisGateway } from '../adapter/out/http/http-weight-analysis.gateway';
import type { GetWeightAnalysisUseCase } from '../application/port/in/get-weight-analysis.use-case';
import type { WeightAnalysisStore } from '../application/port/out/weight-analysis.store';
import { GetWeightAnalysisService } from '../application/service/get-weight-analysis.service';
import {
  ANALYSIS_WEIGHT_PRESENTER_FACTORY,
  AnalysisWeightPresenter,
  type AnalysisWeightPresenterFactory,
} from '../adapter/in/web/weight/presenter/analysis.weight.presenter';

export const GET_WEIGHT_ANALYSIS_USE_CASE = new InjectionToken<GetWeightAnalysisUseCase>(
  'GetWeightAnalysisUseCase',
);

const WEIGHT_ANALYSIS_STORE = new InjectionToken<WeightAnalysisStore>('WeightAnalysisStore');

export function provideAnalysisContext(): EnvironmentProviders {
  return makeEnvironmentProviders([
    {
      provide: WEIGHT_ANALYSIS_STORE,
      useFactory: (httpClient: HttpClient): WeightAnalysisStore =>
        new HttpWeightAnalysisGateway(httpClient),
      deps: [HttpClient],
    },
    {
      provide: GET_WEIGHT_ANALYSIS_USE_CASE,
      useFactory: (store: WeightAnalysisStore): GetWeightAnalysisUseCase =>
        new GetWeightAnalysisService(store),
      deps: [WEIGHT_ANALYSIS_STORE],
    },
  ]);
}

export function provideAnalysisWeightPresenter(): EnvironmentProviders {
  return makeEnvironmentProviders([
    {
      provide: ANALYSIS_WEIGHT_PRESENTER_FACTORY,
      useFactory: (getWeightAnalysis: GetWeightAnalysisUseCase): AnalysisWeightPresenterFactory => {
        return () => new AnalysisWeightPresenter(getWeightAnalysis);
      },
      deps: [GET_WEIGHT_ANALYSIS_USE_CASE],
    },
  ]);
}
