import { ChangeDetectionStrategy, Component, DestroyRef, inject, type OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import {
  JOURNAL_SUMMARY_PRESENTER_FACTORY,
  JournalSummaryPresenter,
  type JournalSummaryPresenterFactory,
} from '../presenter/journal.summary.presenter';

@Component({
  selector: 'app-journal-summary-page',
  templateUrl: './journal.summary.page.html',
  styleUrl: './journal.summary.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: JournalSummaryPresenter,
      useFactory: (factory: JournalSummaryPresenterFactory): JournalSummaryPresenter => factory(),
      deps: [JOURNAL_SUMMARY_PRESENTER_FACTORY],
    },
  ],
})
export class JournalSummaryPage implements OnInit {
  readonly presenter = inject(JournalSummaryPresenter);

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
