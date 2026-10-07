import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  ElementRef,
  inject,
  input,
} from '@angular/core';
import { LineChart } from 'echarts/charts';
import { AriaComponent, GridComponent, VisualMapComponent } from 'echarts/components';
import * as echarts from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { NgxEchartsDirective, provideEchartsCore } from 'ngx-echarts';

import { type SharedGraphView } from '../model/view/shared.graph.model.view';
import { SharedGraphMapper } from './mapper/shared.graph.mapper';
import {
  SHARED_GRAPH_PRESENTER_FACTORY,
  SharedGraphPresenter,
  type SharedGraphPresenterFactory,
} from '../presenter/shared.graph.presenter';

echarts.use([LineChart, GridComponent, VisualMapComponent, AriaComponent, CanvasRenderer]);

@Component({
  selector: 'app-shared-graph',
  imports: [NgxEchartsDirective],
  templateUrl: './shared.graph.page.html',
  styleUrl: './shared.graph.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    provideEchartsCore({ echarts }),
    {
      provide: SharedGraphPresenter,
      useFactory: (factory: SharedGraphPresenterFactory): SharedGraphPresenter => factory(),
      deps: [SHARED_GRAPH_PRESENTER_FACTORY],
    },
  ],
})
export class SharedGraphPage {
  readonly presenter = inject(SharedGraphPresenter);
  readonly state = this.presenter.state;

  private readonly host = inject<ElementRef<HTMLElement>>(ElementRef);
  readonly graph = input.required<SharedGraphView>();

  readonly options = computed(() => {
    const dataState = this.state().dataState;

    if (dataState.kind !== 'data') {
      return {};
    }

    return SharedGraphMapper.modelToOptions(this.resolveGraphColor(dataState.view));
  });

  constructor() {
    effect(() => {
      this.presenter.setData(this.graph());
    });
  }

  private resolveGraphColor(graph: SharedGraphView): SharedGraphView {
    return {
      ...graph,
      series: graph.series.map((series) => ({
        ...series,
        color: this.resolveColor(series.color),
      })),
    };
  }

  private resolveColor(color: string): string {
    if (!color.startsWith('--')) {
      return color;
    }

    const resolvedColor = getComputedStyle(this.host.nativeElement).getPropertyValue(color).trim();

    if (resolvedColor.length === 0) {
      throw new Error(`Shared graph color token "${color}" is not defined.`);
    }

    return resolvedColor;
  }
}
