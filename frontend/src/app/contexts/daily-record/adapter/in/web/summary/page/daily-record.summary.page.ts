import { ChangeDetectionStrategy, Component, DestroyRef, inject, type OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import {
  DAILY_RECORD_SUMMARY_PRESENTER_FACTORY,
  DailyRecordSummaryPresenter,
  type DailyRecordSummaryPresenterFactory,
} from '../presenter/daily-record.summary.presenter';

@Component({
  selector: 'app-daily-record-summary-page',
  imports: [RouterLink],
  templateUrl: './daily-record.summary.page.html',
  styleUrl: './daily-record.summary.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: DailyRecordSummaryPresenter,
      useFactory: (factory: DailyRecordSummaryPresenterFactory): DailyRecordSummaryPresenter =>
        factory(),
      deps: [DAILY_RECORD_SUMMARY_PRESENTER_FACTORY],
    },
  ],
})
export class DailyRecordSummaryPage implements OnInit {
  readonly presenter = inject(DailyRecordSummaryPresenter);

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  ngOnInit(): void {
    this.route.queryParamMap
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((queryParameters) => {
        const selectedDate = queryParameters.get('date') ?? this.presenter.model().selectedDate;

        void this.presenter.selectDate(selectedDate);
      });
  }

  selectDate(intent: string): void {
    void this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        date: intent,
      },
      queryParamsHandling: 'merge',
      replaceUrl: true,
    });
  }
}
