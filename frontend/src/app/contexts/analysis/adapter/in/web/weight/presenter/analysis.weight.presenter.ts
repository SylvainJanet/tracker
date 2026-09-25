import { computed, InjectionToken, signal, type Signal, type WritableSignal } from '@angular/core';
import { catchError, from, map, type Observable, of, take } from 'rxjs';

import type { GetWeightAnalysisUseCase } from '../../../../../application/port/in/get-weight-analysis.use-case';
import { AnalysisWeightModel } from '../model/analysis.weight.model';
import type { AnalysisWeightState } from '../model/state/analysis.weight.model.state';
import { AnalysisWeightMapper } from './mapper/analysis.weight.mapper';

export class AnalysisWeightPresenter {
  private readonly model: WritableSignal<AnalysisWeightModel>;
  readonly state: Signal<AnalysisWeightState>;

  constructor(private readonly getWeightAnalysis: GetWeightAnalysisUseCase) {
    this.model = signal(AnalysisWeightModel.initial());
    this.state = computed(() => this.model().state);
    this.analyze().pipe(take(1)).subscribe();
  }

  get loading(): boolean {
    return this.model().loading;
  }

  analyze(): Observable<void> {
    const transitionEffect = this.model().analysisLoad();

    if (transitionEffect.kind === 'ignored') {
      return of();
    }

    this.model.update(() => transitionEffect.transition);

    return from(this.getWeightAnalysis.get()).pipe(
      map((result) => {
        if (result.kind === 'empty') {
          this.model.update((model) => model.analysisFinishEmpty());
          return;
        }

        this.model.update((model) =>
          model.analysisFinishSuccessful(AnalysisWeightMapper.resultDataToView(result.resultData)),
        );
      }),
      catchError((error) => {
        this.model.update((model) => model.analysisFinishFail(error));
        return of();
      }),
    );
  }
}

export type AnalysisWeightPresenterFactory = () => AnalysisWeightPresenter;

export const ANALYSIS_WEIGHT_PRESENTER_FACTORY = new InjectionToken<AnalysisWeightPresenterFactory>(
  'AnalysisWeightPresenterFactory',
);
