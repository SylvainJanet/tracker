import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ContextNavigationModel } from '../../../../../../../shared/api/shared.context-navigation';

@Component({
  selector: 'app-other-dummy-layout-page',
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './other-dummy.layout.page.html',
  styleUrl: './other-dummy.layout.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OtherDummyLayoutPage {
  readonly navigation = inject(ContextNavigationModel);
}
