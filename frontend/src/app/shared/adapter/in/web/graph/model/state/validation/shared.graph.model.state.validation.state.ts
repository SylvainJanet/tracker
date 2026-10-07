import type { StateKinds } from '../../../../component/model/model';
import type { SharedGraphState } from '../shared.graph.model.state';

export const validStateKinds = new Set<StateKinds<SharedGraphState>>([
  { dataState: 'initial' },
  { dataState: 'data' },
]);
