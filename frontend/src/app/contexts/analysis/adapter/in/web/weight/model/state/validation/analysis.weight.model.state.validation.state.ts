import type { AnalysisWeightState } from '../analysis.weight.model.state';
import type { StateKinds } from '../../../../../../../../../shared/api/shared.component.model';

export const validStateKinds = new Set<StateKinds<AnalysisWeightState>>([
  { analysisState: 'initial' },
  { analysisState: 'loading' },
  { analysisState: 'analyzed' },
  { analysisState: 'empty' },
  { analysisState: 'failure' },
]);
