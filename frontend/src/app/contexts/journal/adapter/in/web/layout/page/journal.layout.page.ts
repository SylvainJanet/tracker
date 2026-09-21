import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ContextNavigationModel } from '../../../../../../../shared/api/shared.context-navigation';

@Component({
  selector: 'app-journal-layout-page',
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './journal.layout.page.html',
  styleUrl: './journal.layout.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class JournalLayoutPage {
  readonly navigation = inject(ContextNavigationModel);
}
