import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  type ElementRef,
  inject,
  type OnInit,
  ViewChild,
} from '@angular/core';

import {
  JOURNAL_LOGGING_PRESENTER_FACTORY,
  JournalLoggingPresenter,
  type JournalLoggingPresenterFactory,
} from '../presenter/journal.logging.presenter';
import { ActivatedRoute, Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-journal-logging-page',
  templateUrl: './journal.logging.page.html',
  styleUrl: './journal.logging.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: JournalLoggingPresenter,
      useFactory: (factory: JournalLoggingPresenterFactory): JournalLoggingPresenter => factory(),
      deps: [JOURNAL_LOGGING_PRESENTER_FACTORY],
    },
  ],
})
export class JournalLoggingPage implements OnInit {
  readonly presenter = inject(JournalLoggingPresenter);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  @ViewChild('confirmationDialog', { static: true })
  private readonly confirmationDialog!: ElementRef<HTMLDialogElement>;

  ngOnInit(): void {
    this.route.queryParamMap
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((queryParameters) => {
        const requestedDate = queryParameters.get('date');

        if (requestedDate !== null) {
          this.presenter.selectDate(requestedDate);
        }
      });
  }

  requestCreation(): void {
    const dialog = this.confirmationDialog.nativeElement;

    if (this.presenter.model().canCreate && !dialog.open) {
      dialog.showModal();
    }
  }

  cancelCreation(): void {
    this.confirmationDialog.nativeElement.close();
  }

  async confirmCreation(): Promise<void> {
    this.confirmationDialog.nativeElement.close();
    await this.presenter.create();
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
