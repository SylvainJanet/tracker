import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ContextNavigationModel } from '../../../../../../../shared/api/shared.context-navigation';

@Component({
  selector: 'app-daily-record-layout-page',
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './daily-record.layout.page.html',
  styleUrl: './daily-record.layout.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DailyRecordLayoutPage {
  readonly navigation = inject(ContextNavigationModel);
}
