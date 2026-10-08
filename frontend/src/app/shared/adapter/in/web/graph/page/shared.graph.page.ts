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
  readonly viewportPointerX: number;
  readonly viewportPointerY: number;
  readonly positionBeforePointer: boolean;
}

interface SharedGraphNodeDetailsContext {
  readonly $implicit: number;
  readonly close: () => void;
}

interface SharedGraphNodeDetailsState {
  readonly value: number;
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
  host: {
    '[class.shared-graph-host--compact]': 'compact()',
  },
})
export class SharedGraphPage {
  readonly presenter = inject(SharedGraphPresenter);
  readonly state = this.presenter.state;

  private readonly host = inject<ElementRef<HTMLElement>>(ElementRef);
  private readonly destroyRef = inject(DestroyRef);
  private chart: echarts.ECharts | undefined;

  readonly tooltipTemplate = contentChild<TemplateRef<SharedGraphTooltipContext>>(TemplateRef);
  readonly tooltipState = signal<SharedGraphTooltipState | undefined>(undefined);
  readonly graphNodeDetailsTemplate =
    contentChild<TemplateRef<SharedGraphNodeDetailsContext>>('graphNodeDetails');

  readonly nodeDetailsState = signal<SharedGraphNodeDetailsState | undefined>(undefined);
  readonly pointerInsidePlottingGrid = signal(false);

  readonly closeNodeDetails = (): void => {
    this.nodeDetailsState.set(undefined);
  };

  private readonly showTooltipHandler = (event: unknown): void => {
    this.tooltipState.set(tooltipStateFrom(event, this.host.nativeElement.getBoundingClientRect()));
  };

  private readonly hideTooltipHandler = (): void => {
    this.tooltipState.set(undefined);
  };

  readonly graph = input.required<SharedGraphView>();
  readonly compact = input(false);

  readonly options = computed(() => {
    const dataState = this.state().dataState;

    if (dataState.kind !== 'data') {
      return {};
    }

    return SharedGraphMapper.modelToOptions(this.resolveGraphColor(dataState.view), {
      compact: this.compact(),
    });
  });

  constructor() {
    effect(() => {
      this.presenter.setData(this.graph());
    });

    this.destroyRef.onDestroy(() => {
      this.disconnectChart();
    });
  }

  onGraphRegionClick(event: MouseEvent): void {
    const chart = this.chart;

    if (chart === undefined || this.graphNodeDetailsTemplate() === undefined) {
      return;
    }

    const value = graphRegionXAxisValueFrom(event, chart, this.graph());

    if (value !== undefined) {
      this.nodeDetailsState.set({ value });
    }
  }

  onGraphPointerMove(event: MouseEvent): void {
    const chart = this.chart;
    const pointer = pointerCoordinatesFrom(event);

    this.pointerInsidePlottingGrid.set(
      chart !== undefined &&
        this.graphNodeDetailsTemplate() !== undefined &&
        pointer !== undefined &&
        chart.containPixel({ gridIndex: 0 }, pointer),
    );
  }

  onGraphPointerLeave(): void {
    this.pointerInsidePlottingGrid.set(false);
  }

  onGraphKeydown(event: KeyboardEvent): void {
    if (
      (event.key !== 'Enter' && event.key !== ' ') ||
      this.graphNodeDetailsTemplate() === undefined
    ) {
      return;
    }

    const value = latestGraphXAxisValue(this.graph());

    if (value === undefined) {
      return;
    }

    event.preventDefault();
    this.nodeDetailsState.set({ value });
  }

  onChartInit(chart: echarts.ECharts): void {
    this.disconnectChart();

    this.chart = chart;
    chart.on('showtip', this.showTooltipHandler);
    chart.on('hidetip', this.hideTooltipHandler);
  }

  private disconnectChart(): void {
    this.pointerInsidePlottingGrid.set(false);

    if (this.chart === undefined) {
      return;
    }

    this.chart.off('showtip', this.showTooltipHandler);
    this.chart.off('hidetip', this.hideTooltipHandler);
    this.chart = undefined;
    this.tooltipState.set(undefined);
    this.nodeDetailsState.set(undefined);
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

function tooltipStateFrom(
  event: unknown,
  graphBounds: DOMRectReadOnly,
): SharedGraphTooltipState | undefined {
  if (!isRecord(event)) {
    return undefined;
  }

  const pointerX = event['x'];
  const pointerY = event['y'];
  const value = xAxisValueFrom(event);

  if (
    typeof pointerX !== 'number' ||
    !Number.isFinite(pointerX) ||
    typeof pointerY !== 'number' ||
    !Number.isFinite(pointerY) ||
    value === undefined
  ) {
    return undefined;
  }

  return {
    value,
    pointerX,
    viewportPointerX: graphBounds.left + pointerX,
    viewportPointerY: graphBounds.top + pointerY,
    positionBeforePointer: graphBounds.width > 0 && pointerX > graphBounds.width / 2,
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

function graphRegionXAxisValueFrom(
  event: unknown,
  chart: echarts.ECharts,
  graph: SharedGraphView,
): number | undefined {
  const pointer = pointerCoordinatesFrom(event);

  if (pointer === undefined || !chart.containPixel({ gridIndex: 0 }, pointer)) {
    return undefined;
  }

  const converted = chart.convertFromPixel({ xAxisIndex: 0 }, pointer[0]);
  const x = Array.isArray(converted) ? converted[0] : converted;

  return typeof x === 'number' && Number.isFinite(x) ? nearestGraphXAxisValue(graph, x) : undefined;
}

function pointerCoordinatesFrom(event: unknown): [number, number] | undefined {
  if (!isRecord(event)) {
    return undefined;
  }

  const offsetX = event['offsetX'];
  const offsetY = event['offsetY'];

  return typeof offsetX === 'number' &&
    Number.isFinite(offsetX) &&
    typeof offsetY === 'number' &&
    Number.isFinite(offsetY)
    ? [offsetX, offsetY]
    : undefined;
}

function nearestGraphXAxisValue(graph: SharedGraphView, target: number): number | undefined {
  let nearest: number | undefined;

  for (const series of graph.series) {
    for (const point of series.points) {
      if (nearest === undefined || Math.abs(point.x - target) < Math.abs(nearest - target)) {
        nearest = point.x;
      }
    }
  }

  return nearest;
}

function latestGraphXAxisValue(graph: SharedGraphView): number | undefined {
  let latest: number | undefined;

  for (const series of graph.series) {
    for (const point of series.points) {
      if (latest === undefined || point.x > latest) {
        latest = point.x;
      }
    }
  }

  return latest;
}
