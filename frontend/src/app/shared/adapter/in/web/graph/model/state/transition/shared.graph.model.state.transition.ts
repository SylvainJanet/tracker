import type { StateTransition } from '../../../../component/model/model';
import type { SharedGraphState } from '../shared.graph.model.state';

export const SharedGraphStateTransitionKind = ['set-data', 'set-axis-interval'] as const;

export type SharedGraphStateTransition = StateTransition<
  SharedGraphState,
  (typeof SharedGraphStateTransitionKind)[number]
>;
