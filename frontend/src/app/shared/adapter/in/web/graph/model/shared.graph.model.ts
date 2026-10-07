import type { SharedGraphState } from './state/shared.graph.model.state';
import { validStateKinds } from './state/validation/shared.graph.model.state.validation.state';
import { validStateTransitions } from './state/validation/shared.graph.model.state.validation.transition';
import type { SharedGraphView } from './view/shared.graph.model.view';
import {
  canTransition,
  ValidState,
  ValidStateTransition,
} from '../../component/model/model.validation.annotation';

export class SharedGraphModel {
  private constructor(readonly state: SharedGraphState) {}

  static initial(): SharedGraphModel {
    return new SharedGraphModel({ dataState: { kind: 'initial' } });
  }

  @ValidState(validStateKinds)
  @ValidStateTransition('set-data', validStateTransitions)
  public setData(view: SharedGraphView): SharedGraphModel {
    return canTransition(this.state, 'set-data', validStateTransitions)
      ? this.withDataState(view)
      : this;
  }

  private withState(state: SharedGraphState): SharedGraphModel {
    return new SharedGraphModel(state);
  }

  private withDataState(view: SharedGraphView): SharedGraphModel {
    return this.withState({ dataState: { kind: 'data', view } });
  }
}
