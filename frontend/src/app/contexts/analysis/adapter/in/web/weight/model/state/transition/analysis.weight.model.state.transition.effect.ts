import type { TransitionEffect } from '../../../../../../../../../shared/api/shared.component.model';
import { type AnalysisWeightModel } from '../../analysis.weight.model';

export const AnalysisTransitionEffectKind = ['analyze'] as const;

interface AnalysisTransitionEffectPayloads {
  analyze: undefined;
}

export type AnalysisTransitionEffect = TransitionEffect<
  (typeof AnalysisTransitionEffectKind)[number],
  AnalysisTransitionEffectPayloads,
  AnalysisWeightModel
>;
