import { computed, InjectionToken, signal, type Signal, type WritableSignal } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { validWeight } from './validator/weight.validator';
import { validCalendarDate } from './validator/date.validator';
import type {
  LogWeightMeasurementCommand,
  LogWeightMeasurementResultData,
  LogWeightMeasurementUseCase,
} from '../../../../../application/port/in/log-weight-measurement.use-case';
import type { GetDefaultJournalDateUseCase } from '../../../../../application/port/in/get-default-journal-date.use-case';
import { catchError, finalize, from, map, type Observable, of } from 'rxjs';
import {
  JournalLoggingModel,
  type JournalLoggingState,
  type LogWeightMeasurementSuccessfulView,
} from '../model/journal.logging.model';

export class JournalLoggingPresenter {
  private readonly model: WritableSignal<JournalLoggingModel>;
  readonly state: Signal<JournalLoggingState>;
  readonly weightForm = new FormGroup({
    weightInKg: new FormControl<number | null>(null, {
      validators: [Validators.required, validWeight],
    }),
  });
  readonly weightControl = this.weightForm.controls.weightInKg;
  readonly dateControl = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, validCalendarDate],
  });
  private readonly submissionAttempted = signal(false);

  constructor(
    private readonly logWeightMeasurementUseCase: LogWeightMeasurementUseCase,
    private readonly getDefaultJournalDateUseCase: GetDefaultJournalDateUseCase,
  ) {
    const defaultDateResult = this.getDefaultJournalDateUseCase.get();
    const defaultDate = defaultDateResult.resultData.defaultJournalDate;
    this.dateControl.setValue(defaultDate);
    this.model = signal(JournalLoggingModel.initial());
    this.state = computed(() => this.model().state);
  }

  validDate(): boolean {
    return this.dateControl.valid;
  }

  canLog(): boolean {
    const validState = !this.loading;
    const validDate = this.dateControl.valid;
    const validWeight = this.weightControl.valid;
    return validState && validDate && validWeight;
  }

  log(): Observable<boolean> {
    this.weightForm.markAllAsTouched();
    this.dateControl.markAsTouched();
    this.submissionAttempted.set(true);

    if (this.weightForm.invalid || this.dateControl.invalid) {
      return of(false);
    }

    const transition = this.model().requestLog();
    this.model.update(() => transition.model);
    if (transition.effect.kind === 'none') {
      return of(false);
    }

    const command = this.toCommand();

    this.dateControl.disable();
    this.weightForm.disable();
    return from(this.logWeightMeasurementUseCase.log(command)).pipe(
      map((result) => {
        this.model.update((model) => model.logSucceeded(this.toView(result.resultData)));
        return true;
      }),
      catchError((error) => {
        this.model.update((model) => model.logFailed(error));
        return of(false);
      }),
      finalize(() => {
        this.dateControl.enable();
        this.weightForm.enable();
      }),
    );
  }

  showWeightErrors(): boolean {
    return this.weightControl.dirty || this.submissionAttempted();
  }

  cancelLog(): void {
    this.resetForm();
    this.model.update((model) => model.cancelRequest());
  }

  resetForm(): void {
    this.weightForm.reset();
    this.submissionAttempted.set(false);
  }

  get loading(): boolean {
    return this.model().loading;
  }

  selectDate(newDate: string): void {
    this.dateControl.setValue(newDate);
    this.model.update((model) => model.dateChanged());
  }

  private toCommand(): LogWeightMeasurementCommand {
    return {
      date: this.dateControl.value,
      weightInKg: this.weightControl.value!,
    };
  }

  private toView(result: LogWeightMeasurementResultData): LogWeightMeasurementSuccessfulView {
    return {
      date: result.date,
      weightInKg: result.weightInKg,
    };
  }
}

export type JournalLoggingPresenterFactory = () => JournalLoggingPresenter;

export const JOURNAL_LOGGING_PRESENTER_FACTORY = new InjectionToken<JournalLoggingPresenterFactory>(
  'JournalLoggingPresenterFactory',
);
