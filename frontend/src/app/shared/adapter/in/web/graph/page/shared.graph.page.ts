import {
  ChangeDetectionStrategy,
  Component,
  computed,
  ElementRef,
  inject,
  input,
} from '@angular/core';
import { LineChart } from 'echarts/charts';
import { AriaComponent, GridComponent, VisualMapComponent } from 'echarts/components';
import * as echarts from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { NgxEchartsDirective, provideEchartsCore } from 'ngx-echarts';

import type { SharedGraphModel } from '../model/shared.graph.model';
import { SharedGraphMapper } from '../presenter/mapper/shared.graph.mapper';

echarts.use([LineChart, GridComponent, VisualMapComponent, AriaComponent, CanvasRenderer]);

@Component({
  selector: 'app-shared-graph',
  imports: [NgxEchartsDirective],
  templateUrl: './shared.graph.page.html',
  styleUrl: './shared.graph.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [provideEchartsCore({ echarts })],
})
export class SharedGraphPage {
  private readonly host = inject<ElementRef<HTMLElement>>(ElementRef);

  readonly graph = input.required<SharedGraphModel>();
  readonly options = computed(() => {
    const graph = this.graph();

    return SharedGraphMapper.modelToOptions({
      ...graph,
      series: graph.series.map((series) => ({
        ...series,
        color: this.resolveColor(series.color),
      })),
    });
  });

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
