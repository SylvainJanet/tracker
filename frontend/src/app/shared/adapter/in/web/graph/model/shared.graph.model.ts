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

  @ValidState(validStateKinds)
  @ValidStateTransition('set-axis-interval', validStateTransitions)
  public setAxisInterval(axis: 'xAxis' | 'yAxis', interval: number | undefined): SharedGraphModel {
    if (!canTransition(this.state, 'set-axis-interval', validStateTransitions)) {
      return this;
    }

    const dataState = this.state.dataState;

    if (dataState.kind !== 'data') {
      return this;
    }

    const availableIntervals = dataState.view.presentation?.[axis].policy?.intervals;

    if (
      interval !== undefined &&
      (availableIntervals === undefined ||
        !availableIntervals.some((candidate) => candidate.interval === interval))
    ) {
      return this;
    }

    const currentOverrides = dataState.intervalOverrides ?? {};
    const intervalOverrides: {
      xAxis?: number;
      yAxis?: number;
    } = {};

    if (axis !== 'xAxis' && currentOverrides.xAxis !== undefined) {
      intervalOverrides.xAxis = currentOverrides.xAxis;
    }

    if (axis !== 'yAxis' && currentOverrides.yAxis !== undefined) {
      intervalOverrides.yAxis = currentOverrides.yAxis;
    }

    if (interval !== undefined) {
      intervalOverrides[axis] = interval;
    }

    return this.withState({
      dataState: {
        ...dataState,
        intervalOverrides,
      },
    });
  }

  private withState(state: SharedGraphState): SharedGraphModel {
    return new SharedGraphModel(state);
  }

  private withDataState(view: SharedGraphView): SharedGraphModel {
    return this.withState({ dataState: { kind: 'data', view } });
  }
}
