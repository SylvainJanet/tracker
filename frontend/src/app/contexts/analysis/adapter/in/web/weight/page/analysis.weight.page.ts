import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import {
  SharedGraphPage,
  SharedGraphRollingAverageDialogPage,
  SharedGraphRollingAverageTooltipPage,
} from '../../../../../../../shared/api/shared.graph';
import { SharedSpinnerPage } from '../../../../../../../shared/api/shared.spinner';
import {
  ANALYSIS_WEIGHT_PRESENTER_FACTORY,
  AnalysisWeightPresenter,
  type AnalysisWeightPresenterFactory,
} from '../presenter/analysis.weight.presenter';
import { take } from 'rxjs';

@Component({
  selector: 'app-analysis-weight-page',
  imports: [
    SharedSpinnerPage,
    SharedGraphPage,
    SharedGraphRollingAverageTooltipPage,
    SharedGraphRollingAverageDialogPage,
  ],
  templateUrl: './analysis.weight.page.html',
  styleUrl: './analysis.weight.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: AnalysisWeightPresenter,
      useFactory: (factory: AnalysisWeightPresenterFactory): AnalysisWeightPresenter => factory(),
      deps: [ANALYSIS_WEIGHT_PRESENTER_FACTORY],
    },
  ],
})
export class AnalysisWeightPage {
  readonly presenter = inject(AnalysisWeightPresenter);
  readonly state = this.presenter.state;

  retry(): void {
    this.presenter.analyze().pipe(take(1)).subscribe();
  }
}
