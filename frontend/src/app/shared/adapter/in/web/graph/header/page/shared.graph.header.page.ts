import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-shared-graph-header',
  templateUrl: './shared.graph.header.page.html',
  styleUrl: './shared.graph.header.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SharedGraphHeaderPage {
  readonly title = input.required<string>();
}
