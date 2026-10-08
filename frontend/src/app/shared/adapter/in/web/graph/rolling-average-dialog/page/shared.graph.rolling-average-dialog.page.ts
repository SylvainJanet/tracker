import {
  type AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  type ElementRef,
  input,
  output,
  viewChild,
} from '@angular/core';

import type { SharedRollingAverageGraphDialogView } from '../../model/view/shared.graph.model.view';
import { SharedGraphPage } from '../../page/shared.graph.page';
import { SharedGraphRollingAverageTooltipPage } from '../../rolling-average-tooltip/page/shared.graph.rolling-average-tooltip.page';

@Component({
  selector: 'app-shared-graph-rolling-average-dialog',
  imports: [SharedGraphPage, SharedGraphRollingAverageTooltipPage],
  templateUrl: './shared.graph.rolling-average-dialog.page.html',
  styleUrl: './shared.graph.rolling-average-dialog.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SharedGraphRollingAverageDialogPage implements AfterViewInit {
  readonly view = input.required<SharedRollingAverageGraphDialogView>();
  readonly closed = output<void>();

  private readonly dialog = viewChild.required<ElementRef<HTMLDialogElement>>('dialog');

  ngAfterViewInit(): void {
    this.dialog().nativeElement.showModal();
  }

  close(): void {
    this.dialog().nativeElement.close();
  }

  dialogClosed(): void {
    this.closed.emit();
  }
}
