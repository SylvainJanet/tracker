import type { StateTransition } from '../../../../../../../../shared/api/shared.component.model';
import type { AnalysisWeightState } from './analysis.weight.model.state';

export const AnalysisStateTransitionKind = ['analysis-load', 'analysis-finish'] as const;

export type AnalysisStateTransition = StateTransition<
  AnalysisWeightState,
  (typeof AnalysisStateTransitionKind)[number]
>;
