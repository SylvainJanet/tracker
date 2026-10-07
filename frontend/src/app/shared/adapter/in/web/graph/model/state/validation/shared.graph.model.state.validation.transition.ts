import type { SharedGraphStateTransition } from '../transition/shared.graph.model.state.transition';

export const validStateTransitions = new Set<SharedGraphStateTransition>([
  {
    kind: 'set-data',
    transitions: [{ from: { dataState: 'initial' }, to: [{ dataState: 'data' }] }],
  },
]);
