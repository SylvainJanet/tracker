import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  ElementRef,
  inject,
  input,
  contentChild,
  DestroyRef,
  signal,
  TemplateRef,
} from '@angular/core';
import { LineChart } from 'echarts/charts';
import { NgTemplateOutlet } from '@angular/common';
import {
  AriaComponent,
  GridComponent,
  TooltipComponent,
  VisualMapComponent,
} from 'echarts/components';
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

echarts.use([
  LineChart,
  GridComponent,
  TooltipComponent,
  VisualMapComponent,
  AriaComponent,
  CanvasRenderer,
]);

interface SharedGraphTooltipContext {
  readonly $implicit: number;
}

interface SharedGraphTooltipState {
  readonly value: number;
  readonly pointerX: number;
  readonly positionBeforePointer: boolean;
}

@Component({
  selector: 'app-shared-graph',
  imports: [NgTemplateOutlet, NgxEchartsDirective],
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
  private readonly destroyRef = inject(DestroyRef);
  private chart: echarts.ECharts | undefined;

  readonly tooltipTemplate = contentChild<TemplateRef<SharedGraphTooltipContext>>(TemplateRef);
  readonly tooltipState = signal<SharedGraphTooltipState | undefined>(undefined);

  private readonly showTooltipHandler = (event: unknown): void => {
    this.tooltipState.set(tooltipStateFrom(event, this.host.nativeElement.clientWidth));
  };

  private readonly hideTooltipHandler = (): void => {
    this.tooltipState.set(undefined);
  };
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

    this.destroyRef.onDestroy(() => {
      this.disconnectChart();
    });
  }

  onChartInit(chart: echarts.ECharts): void {
    this.disconnectChart();

    this.chart = chart;
    chart.on('showtip', this.showTooltipHandler);
    chart.on('hidetip', this.hideTooltipHandler);
  }

  private disconnectChart(): void {
    if (this.chart === undefined) {
      return;
    }

    this.chart.off('showtip', this.showTooltipHandler);
    this.chart.off('hidetip', this.hideTooltipHandler);
    this.chart = undefined;
    this.tooltipState.set(undefined);
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

function tooltipStateFrom(event: unknown, graphWidth: number): SharedGraphTooltipState | undefined {
  if (!isRecord(event)) {
    return undefined;
  }

  const pointerX = event['x'];
  const value = xAxisValueFrom(event);

  if (typeof pointerX !== 'number' || !Number.isFinite(pointerX) || value === undefined) {
    return undefined;
  }

  return {
    value,
    pointerX,
    positionBeforePointer: graphWidth > 0 && pointerX > graphWidth / 2,
  };
}

function xAxisValueFrom(event: Record<string, unknown>): number | undefined {
  const coordinateSystems = event['dataByCoordSys'];

  if (!Array.isArray(coordinateSystems)) {
    return undefined;
  }

  for (const coordinateSystem of coordinateSystems) {
    if (!isRecord(coordinateSystem)) {
      continue;
    }

    const axes = coordinateSystem['dataByAxis'];

    if (!Array.isArray(axes)) {
      continue;
    }

    for (const axis of axes) {
      if (
        isRecord(axis) &&
        axis['axisDim'] === 'x' &&
        typeof axis['value'] === 'number' &&
        Number.isFinite(axis['value'])
      ) {
        return axis['value'];
      }
    }
  }

  return undefined;
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null;
}
