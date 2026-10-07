import { computed, InjectionToken, signal, type Signal, type WritableSignal } from '@angular/core';
import { SharedGraphModel } from '../model/shared.graph.model';
import type { SharedGraphState } from '../model/state/shared.graph.model.state';
import type { SharedGraphView } from '../model/view/shared.graph.model.view';

export class SharedGraphPresenter {
  private readonly model: WritableSignal<SharedGraphModel>;
  readonly state: Signal<SharedGraphState>;

  constructor() {
    this.model = signal(SharedGraphModel.initial());
    this.state = computed(() => this.model().state);
  }

  setData(view: SharedGraphView) {
    this.model.update((model) => model.setData(view));
  }
}

export type SharedGraphPresenterFactory = () => SharedGraphPresenter;

export const SHARED_GRAPH_PRESENTER_FACTORY = new InjectionToken<SharedGraphPresenterFactory>(
  'SharedGraphPresenterFactory',
);
