import type { StateDimension } from '../../../../../../../../../shared/api/shared.component.model';
import type { AnalysisWeightView } from '../../view/analysis.weight.model.view';

export const AnalysisStateKind = ['initial', 'loading', 'analyzed', 'empty', 'failure'] as const;

interface AnalysisStatePayloads {
  initial: undefined;
  loading: undefined;
  analyzed: {
    readonly view: AnalysisWeightView;
  };
  empty: {
    readonly title: string;
    readonly message: string;
  };
  failure: {
    readonly title: string;
    readonly message: string;
  };
}

export type AnalysisState = StateDimension<
  (typeof AnalysisStateKind)[number],
  AnalysisStatePayloads
>;
