import {
  type AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  input,
  output,
  viewChild,
} from '@angular/core';

import type { SharedRollingAverageGraphDialogView } from '../../model/view/shared.graph.model.view';
import { SharedGraphPage } from '../../page/shared.graph.page';
import { SharedGraphRollingAverageTooltipPage } from '../../rolling-average-tooltip/page/shared.graph.rolling-average-tooltip.page';
import { SharedModalPage } from '../../../modal/page/shared.modal.page';

@Component({
  selector: 'app-shared-graph-rolling-average-dialog',
  imports: [SharedGraphPage, SharedGraphRollingAverageTooltipPage, SharedModalPage],
  templateUrl: './shared.graph.rolling-average-dialog.page.html',
  styleUrl: './shared.graph.rolling-average-dialog.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SharedGraphRollingAverageDialogPage implements AfterViewInit {
  readonly view = input.required<SharedRollingAverageGraphDialogView>();
  readonly closed = output<void>();

  private readonly dialog = viewChild.required(SharedModalPage);

  ngAfterViewInit(): void {
    this.dialog().open();
  }

  close(): void {
    this.dialog().close();
  }
}
