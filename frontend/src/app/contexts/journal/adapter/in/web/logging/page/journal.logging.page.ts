import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  type ElementRef,
  inject,
  type OnInit,
  ViewChild,
} from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  JOURNAL_LOGGING_PRESENTER_FACTORY,
  JournalLoggingPresenter,
  type JournalLoggingPresenterFactory,
} from '../presenter/journal.logging.presenter';

@Component({
  selector: 'app-journal-logging-page',
  templateUrl: './journal.logging.page.html',
  styleUrl: './journal.logging.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule],
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
  readonly state = this.presenter.state;
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  @ViewChild('logWeightMeasurementDialog', { static: true })
  private readonly logWeightMeasurementDialog!: ElementRef<HTMLDialogElement>;

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

  openDialog(): void {
    const dialog = this.logWeightMeasurementDialog.nativeElement;
    if (this.presenter.validDate() && !dialog.open) {
      dialog.returnValue = '';
      dialog.showModal();
    }
  }

  dialogClosed(): void {
    const logged = this.logWeightMeasurementDialog.nativeElement.returnValue === 'logged';

    if (logged) {
      this.presenter.resetForm();
    } else {
      this.presenter.cancelLog();
    }
  }

  cancelLog(): void {
    this.logWeightMeasurementDialog.nativeElement.close('canceled');
  }

  submitLog(): void {
    this.presenter
      .log()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((logged) => {
        if (logged) {
          this.logWeightMeasurementDialog.nativeElement.close('logged');
        }
      });
  }

  selectDate(newDate: string): void {
    void this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        date: newDate,
      },
      queryParamsHandling: 'merge',
      replaceUrl: true,
    });
  }
}
