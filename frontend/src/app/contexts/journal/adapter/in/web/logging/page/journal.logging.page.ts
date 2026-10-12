import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  inject,
  type OnInit,
  viewChild,
} from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  JOURNAL_LOGGING_PRESENTER_FACTORY,
  JournalLoggingPresenter,
  type JournalLoggingPresenterFactory,
} from '../presenter/journal.logging.presenter';
import { SharedSpinnerPage } from '../../../../../../../shared/api/shared.spinner';
import { concatMap, map, take } from 'rxjs';
import { SharedModalPage } from '../../../../../../../shared/api/shared.modal';

@Component({
  selector: 'app-journal-logging-page',
  templateUrl: './journal.logging.page.html',
  styleUrl: './journal.logging.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, SharedSpinnerPage, SharedModalPage],
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

  private readonly logWeightMeasurementDialog = viewChild.required(SharedModalPage);

  ngOnInit(): void {
    this.route.queryParamMap
      .pipe(
        map((queryParameters) => queryParameters.get('date') ?? this.presenter.dateControl.value),
        concatMap((selectedDate) => this.presenter.selectDate(selectedDate).pipe(take(1))),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe();
  }

  openDialog(): void {
    this.logWeightMeasurementDialog().open();
  }

  dialogClosed(returnValue: string): void {
    if (returnValue === 'logged') {
      this.presenter.resetForm();
    } else {
      this.presenter.cancelLog();
    }
  }

  cancelLog(): void {
    this.logWeightMeasurementDialog().close('canceled');
  }

  submitLog(): void {
    this.presenter
      .log()
      .pipe(take(1))
      .subscribe((logged) => {
        if (logged) {
          this.logWeightMeasurementDialog().close('logged');
        }
      });
  }

  retryGet(): void {
    this.presenter.selectDate(this.presenter.dateControl.value).pipe(take(1)).subscribe();
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
