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
import { JournalLoggingModel } from '../model/journal.logging.model';
import type {
  GetWeightMeasurementByDateQuery,
  GetWeightMeasurementByDateResultData,
  GetWeightMeasurementByDateUseCase,
} from '../../../../../application/port/in/get-weight-measurement-by-date.use-case';
import type { JournalLoggingState } from '../model/state/journal.logging.model.state';
import type {
  GetSuccessfulView,
  LogSuccessfulView,
} from '../model/view/journal.logging.model.view';
import { validStateTransitions } from '../model/state/validation/journal.logging.model.state.validation.transition';
import { canTransition } from '../../../../../../../shared/api/shared.component.model';
import { Weight } from '../../../../../../../shared/api/shared.weight-measurement';

export class JournalLoggingPresenter {
  private readonly model: WritableSignal<JournalLoggingModel>;
  readonly state: Signal<JournalLoggingState>;
  readonly weightStepInKg = Weight.measurementIncrementInKilograms();

  readonly weightForm = new FormGroup({
    weightInKg: new FormControl<number | null>(null, {
      validators: [Validators.required, validWeight],
    }),
  });
  readonly weightControl = this.weightForm.controls.weightInKg;
  readonly dateControl = new FormControl(
    { value: '', disabled: false },
    {
      nonNullable: true,
      validators: [Validators.required, validCalendarDate],
    },
  );
  private readonly weightSubmissionAttempted = signal(false);

  constructor(
    private readonly logWeightMeasurementUseCase: LogWeightMeasurementUseCase,
    private readonly getWeightMeasurementByDateUseCase: GetWeightMeasurementByDateUseCase,
    private readonly getDefaultJournalDateUseCase: GetDefaultJournalDateUseCase,
  ) {
    const defaultDateResult = this.getDefaultJournalDateUseCase.get();
    const defaultDate = defaultDateResult.resultData.defaultJournalDate;
    this.dateControl.setValue(defaultDate);
    this.model = signal(JournalLoggingModel.initial());
    this.state = computed(() => this.model().state);
  }

  get logLoading(): boolean {
    return this.model().logLoading;
  }

  get getLoading(): boolean {
    return this.model().getLoading;
  }

  validDate(): boolean {
    return this.dateControl.valid;
  }

  canEditWeightToLog(): boolean {
    const validTransitionState = canTransition(this.state(), 'log-load', validStateTransitions);
    return this.validDate() && validTransitionState;
  }

  showWeightErrors(): boolean {
    return this.weightControl.dirty || this.weightSubmissionAttempted();
  }

  showPageMainSection() {
    return this.validDate() || this.getLoading || this.logLoading;
  }

  cancelLog(): void {
    this.resetForm();
    if (canTransition(this.model().state, 'log-cancel', validStateTransitions)) {
      this.model.update((model) => model.logCancel());
    }
  }

  resetForm(): void {
    this.weightForm.reset();
    this.weightSubmissionAttempted.set(false);
  }

  log(): Observable<boolean> {
    this.weightForm.markAllAsTouched();
    this.dateControl.markAsTouched();
    this.weightSubmissionAttempted.set(true);

    if (this.weightForm.invalid || this.dateControl.invalid) {
      return of(false);
    }

    const transitionEffect = this.model().logLoad();

    if (transitionEffect.kind === 'ignored') {
      return of(false);
    }

    this.model.update(() => transitionEffect.transition);

    const command = this.logCommand();

    this.dateControl.disable();
    this.weightForm.disable();

    return from(this.logWeightMeasurementUseCase.log(command)).pipe(
      map((result) => {
        this.model.update((model) => model.logFinishSuccess(this.logView(result.resultData)));
        return true;
      }),
      catchError((error) => {
        this.model.update((model) => model.logFinishFail(error));
        return of(false);
      }),
      finalize(() => {
        this.dateControl.enable();
        this.weightForm.enable();
      }),
    );
  }

  selectDate(newDate: string): Observable<void> {
    this.dateControl.setValue(newDate);
    this.dateControl.markAsTouched();

    if (this.dateControl.invalid) {
      return of();
    }

    const transitionEffect = this.model().getLoad();

    if (transitionEffect.kind === 'ignored') {
      return of();
    }

    this.model.update(() => transitionEffect.transition);

    const query = this.getQuery();

    this.dateControl.disable();
    this.weightForm.disable();

    return from(this.getWeightMeasurementByDateUseCase.get(query)).pipe(
      map((result) => {
        if (result.kind === 'found') {
          this.model.update((model) => model.getFinishSuccess(this.getView(result.resultData)));
        } else {
          this.model.update((model) => model.getFinishNotFound());
        }
      }),
      catchError((error) => {
        this.model.update((model) => model.getFinishFailed(error));
        return of();
      }),
      finalize(() => {
        this.dateControl.enable();
        this.weightForm.enable();
      }),
    );
  }

  private logCommand(): LogWeightMeasurementCommand {
    return {
      date: this.dateControl.value,
      weightInKg: this.weightControl.value!,
    };
  }

  private getQuery(): GetWeightMeasurementByDateQuery {
    return {
      date: this.dateControl.value,
    };
  }

  private logView(result: LogWeightMeasurementResultData): LogSuccessfulView {
    return {
      date: result.date,
      weightInKg: result.weightInKg,
    };
  }

  private getView(result: GetWeightMeasurementByDateResultData): GetSuccessfulView {
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
