import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import type { SharedRollingAverageGraphTooltipView } from '../../model/view/shared.graph.model.view';

@Component({
  selector: 'app-shared-graph-rolling-average-tooltip',
  styleUrl: './shared.graph.rolling-average-tooltip.page.scss',
  templateUrl: './shared.graph.rolling-average-tooltip.page.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SharedGraphRollingAverageTooltipPage {
  readonly view = input.required<SharedRollingAverageGraphTooltipView>();
}
