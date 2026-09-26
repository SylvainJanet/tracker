import {
  canTransition,
  ValidState,
  ValidStateTransition,
  ValidStateTransitionEffect,
} from '../../../../../../../shared/api/shared.component.model';
import type { AnalysisWeightState } from './state/analysis.weight.model.state';
import { validStateKinds } from './state/validation/analysis.weight.model.state.validation.state';
import { validStateTransitions } from './state/validation/analysis.weight.model.state.validation.transition';
import type { AnalysisTransitionEffect } from './state/transition/analysis.weight.model.state.transition.effect';
import type { AnalysisWeightView } from './view/analysis.weight.model.view';

export class AnalysisWeightModel {
  private constructor(readonly state: AnalysisWeightState) {}

  static initial(): AnalysisWeightModel {
    return new AnalysisWeightModel({ analysisState: { kind: 'initial' } });
  }

  get loading(): boolean {
    return this.state.analysisState.kind === 'loading';
  }

  @ValidState(validStateKinds)
  @ValidStateTransitionEffect('analysis-load', validStateTransitions)
  public analysisLoad(): AnalysisTransitionEffect {
    if (!canTransition(this.state, 'analysis-load', validStateTransitions)) {
      return {
        kind: 'ignored',
        transition: this,
      };
    }

    return {
      kind: 'accepted',
      transition: this.withAnalysisLoadingState(),
      effect: {
        kind: 'analyze',
      },
    };
  }

  @ValidState(validStateKinds)
  @ValidStateTransition('analysis-finish', validStateTransitions)
  public analysisFinishSuccessful(view: AnalysisWeightView): AnalysisWeightModel {
    return canTransition(this.state, 'analysis-finish', validStateTransitions)
      ? this.withAnalysisSuccessfulState(view)
      : this;
  }

  @ValidState(validStateKinds)
  @ValidStateTransition('analysis-finish', validStateTransitions)
  public analysisFinishEmpty(): AnalysisWeightModel {
    return canTransition(this.state, 'analysis-finish', validStateTransitions)
      ? this.withAnalysisEmptyState()
      : this;
  }

  @ValidState(validStateKinds)
  @ValidStateTransition('analysis-finish', validStateTransitions)
  public analysisFinishFail(error: unknown): AnalysisWeightModel {
    if (!canTransition(this.state, 'analysis-finish', validStateTransitions)) {
      return this;
    }
    if (typeof error === 'string' || error instanceof Error) {
      const errorMessage = typeof error === 'string' ? error : error.message;
      return this.withAnalysisFailureState(errorMessage);
    }
    return this.withAnalysisFailureState('unknown error');
  }

  private withState(state: AnalysisWeightState): AnalysisWeightModel {
    return new AnalysisWeightModel(state);
  }

  private withAnalysisLoadingState(): AnalysisWeightModel {
    return this.withState({ analysisState: { kind: 'loading' } });
  }

  private withAnalysisSuccessfulState(view: AnalysisWeightView): AnalysisWeightModel {
    return this.withState({ analysisState: { kind: 'analyzed', view } });
  }

  private withAnalysisEmptyState(): AnalysisWeightModel {
    return this.withState({
      analysisState: {
        kind: 'empty',
        message: 'Nothing to display: no weight measurement was found.',
        title: 'No weight measurement found',
      },
    });
  }

  private withAnalysisFailureState(errorMessage: string): AnalysisWeightModel {
    return this.withState({
      analysisState: {
        kind: 'failure',
        title: 'Unable to perform weight analysis',
        message: `The weight analysis could not be performed: ${errorMessage}`,
      },
    });
  }
}
