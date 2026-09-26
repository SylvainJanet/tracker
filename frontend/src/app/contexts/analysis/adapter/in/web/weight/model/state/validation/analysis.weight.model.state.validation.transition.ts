import type { AnalysisStateTransition } from '../analysis.weight.model.state.transition';

export const validStateTransitions = new Set<AnalysisStateTransition>([
  {
    kind: 'analysis-load',
    transitions: [
      {
        from: { analysisState: 'initial' },
        to: [{ analysisState: 'loading' }],
      },
      {
        from: { analysisState: 'failure' },
        to: [{ analysisState: 'loading' }],
      },
    ],
  },
  {
    kind: 'analysis-finish',
    transitions: [
      {
        from: { analysisState: 'loading' },
        to: [
          { analysisState: 'analyzed' },
          { analysisState: 'failure' },
          { analysisState: 'empty' },
        ],
      },
    ],
  },
]);
