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
  type TemplateRef,
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
import { SharedGraphInteractionMapper } from './mapper/shared.graph.interaction.mapper';

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

  readonly tooltipTemplate = contentChild<TemplateRef<SharedGraphTooltipContext>>('graphTooltip');
  readonly tooltipState =
    signal<ReturnType<typeof SharedGraphInteractionMapper.tooltipStateFrom>>(undefined);
  readonly graphNodeDetailsTemplate =
    contentChild<TemplateRef<SharedGraphNodeDetailsContext>>('graphNodeDetails');

  readonly nodeDetailsState = signal<SharedGraphNodeDetailsState | undefined>(undefined);
  readonly pointerInsidePlottingGrid = signal(false);

  readonly closeNodeDetails = (): void => {
    this.nodeDetailsState.set(undefined);
  };

  private readonly showTooltipHandler = (event: unknown): void => {
    const graphBounds = this.host.nativeElement.getBoundingClientRect();
    const viewportWidth = this.compact()
      ? this.host.nativeElement.ownerDocument.defaultView?.innerWidth
      : undefined;

    this.tooltipState.set(
      SharedGraphInteractionMapper.tooltipStateFrom(event, graphBounds, viewportWidth),
    );
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

    const value = SharedGraphInteractionMapper.graphRegionXAxisValueFrom(
      event,
      chart,
      this.graph(),
    );

    if (value !== undefined) {
      this.nodeDetailsState.set({ value });
    }
  }

  onGraphPointerMove(event: MouseEvent): void {
    const chart = this.chart;
    const pointer = SharedGraphInteractionMapper.pointerCoordinatesFrom(event);

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

    const value = SharedGraphInteractionMapper.latestGraphXAxisValue(this.graph());

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

    const chart = this.chart;

    this.chart = undefined;
    this.tooltipState.set(undefined);
    this.nodeDetailsState.set(undefined);

    if (chart === undefined || chart.isDisposed()) {
      return;
    }

    chart.off('showtip', this.showTooltipHandler);
    chart.off('hidetip', this.hideTooltipHandler);
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
