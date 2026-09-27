import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { LineChart } from 'echarts/charts';
import { AriaComponent, GridComponent } from 'echarts/components';
import * as echarts from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { NgxEchartsDirective, provideEchartsCore } from 'ngx-echarts';

import type { SharedGraphModel } from '../model/shared.graph.model';
import { SharedGraphMapper } from '../presenter/mapper/shared.graph.mapper';

echarts.use([LineChart, GridComponent, AriaComponent, CanvasRenderer]);

@Component({
  selector: 'app-shared-graph',
  imports: [NgxEchartsDirective],
  templateUrl: './shared.graph.page.html',
  styleUrl: './shared.graph.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [provideEchartsCore({ echarts })],
})
export class SharedGraphPage {
  readonly graph = input.required<SharedGraphModel>();
  readonly options = computed(() => SharedGraphMapper.modelToOptions(this.graph()));
}
