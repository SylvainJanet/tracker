import type { StateDimension } from '../../../../component/model/model';
import { type SharedGraphView } from '../../view/shared.graph.model.view';

export const SharedGraphDataStateKind = ['initial', 'data'] as const;

interface SharedGraphDataStatePayloads {
  initial: undefined;
  data: {
    readonly view: SharedGraphView;
  };
}

export type SharedGraphDataState = StateDimension<
  (typeof SharedGraphDataStateKind)[number],
  SharedGraphDataStatePayloads
>;
