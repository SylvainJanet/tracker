import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ContextNavigationModel } from '../../../../../../../shared/api/shared.context-navigation';

@Component({
  selector: 'app-dummy-layout-page',
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './dummy.layout.page.html',
  styleUrl: './dummy.layout.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DummyLayoutPage {
  readonly navigation = inject(ContextNavigationModel);
}
