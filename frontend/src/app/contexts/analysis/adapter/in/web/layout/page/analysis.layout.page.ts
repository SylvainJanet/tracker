import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { ContextNavigationModel } from '../../../../../../../shared/api/shared.context-navigation';

@Component({
  selector: 'app-analysis-layout-page',
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './analysis.layout.page.html',
  styleUrl: './analysis.layout.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AnalysisLayoutPage {
  readonly navigation = inject(ContextNavigationModel);
}
