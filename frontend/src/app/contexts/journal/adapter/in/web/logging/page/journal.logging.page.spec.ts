import { signal, type WritableSignal } from '@angular/core';
import { type ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, convertToParamMap, type ParamMap, Router } from '@angular/router';
import { BehaviorSubject, of, Subject } from 'rxjs';
import { beforeEach, describe, expect, it, type Mock, vi } from 'vitest';

import type { JournalLoggingState } from '../model/state/journal.logging.model.state';
import {
  JOURNAL_LOGGING_PRESENTER_FACTORY,
  type JournalLoggingPresenter,
} from '../presenter/journal.logging.presenter';
import { JournalLoggingPage } from './journal.logging.page';
type JournalLoggingPresenterFake = Pick<JournalLoggingPresenter, keyof JournalLoggingPresenter>;
describe('JournalLoggingPage', () => {
  const defaultDate = '2026-09-20';

  let fixture: ComponentFixture<JournalLoggingPage>;

  let state: WritableSignal<JournalLoggingState>;
  let dateControl: FormControl<string>;
  let weightControl: FormControl<number | null>;
  let weightForm: FormGroup<{
    weightInKg: FormControl<number | null>;
  }>;

  let dateValid: WritableSignal<boolean>;
  let showPageMainSection: WritableSignal<boolean>;
  let canEditWeight: WritableSignal<boolean>;

  let log: Mock<JournalLoggingPresenterFake['log']>;
  let resetForm: Mock<JournalLoggingPresenterFake['resetForm']>;
  let cancelPresenterLog: Mock<JournalLoggingPresenterFake['cancelLog']>;
  let selectPresenterDate: Mock<JournalLoggingPresenterFake['selectDate']>;

  let queryParamMap: BehaviorSubject<ParamMap>;
  let activatedRoute: {
    readonly queryParamMap: BehaviorSubject<ParamMap>;
  };
  let navigate: ReturnType<typeof vi.fn>;

  let dialog: HTMLDialogElement;
  let showModal: ReturnType<typeof vi.fn>;
  let close: ReturnType<typeof vi.fn>;

  beforeEach(async () => {
    state = signal<JournalLoggingState>({
      logState: { kind: 'idle' },
      getState: { kind: 'idle' },
    });
    dateValid = signal(true);
    showPageMainSection = signal(true);
    canEditWeight = signal(true);

    dateControl = new FormControl(defaultDate, {
      nonNullable: true,
    });
    weightControl = new FormControl<number | null>(null);
    weightForm = new FormGroup({
      weightInKg: weightControl,
    });

    log = vi.fn<JournalLoggingPresenterFake['log']>(() => of(false));
    selectPresenterDate = vi.fn<JournalLoggingPresenterFake['selectDate']>((newDate) => {
      dateControl.setValue(newDate);
      return of(undefined);
    });
    resetForm = vi.fn<JournalLoggingPresenterFake['resetForm']>();
    cancelPresenterLog = vi.fn<JournalLoggingPresenterFake['cancelLog']>();

    const presenter = {
      state: state.asReadonly(),
      dateControl,
      weightControl,
      weightForm,
      weightStepInKg: 0.1,

      get logLoading(): boolean {
        return state().logState.kind === 'loading';
      },

      get getLoading(): boolean {
        return state().getState.kind === 'loading';
      },

      validDate: (): boolean => dateValid(),
      showPageMainSection: (): boolean => showPageMainSection(),
      canEditWeightToLog: (): boolean => canEditWeight(),
      selectDate: selectPresenterDate,
      showWeightErrors: (): boolean => false,
      log,
      resetForm,
      cancelLog: cancelPresenterLog,
    } satisfies JournalLoggingPresenterFake;

    queryParamMap = new BehaviorSubject<ParamMap>(convertToParamMap({}));
    activatedRoute = {
      queryParamMap,
    };

    navigate = vi.fn(async () => true);

    await TestBed.configureTestingModule({
      imports: [JournalLoggingPage],
      providers: [
        {
          provide: JOURNAL_LOGGING_PRESENTER_FACTORY,
          useValue: (): JournalLoggingPresenterFake => presenter,
        },
        {
          provide: ActivatedRoute,
          useValue: activatedRoute,
        },
        {
          provide: Router,
          useValue: {
            navigate,
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(JournalLoggingPage);
    fixture.detectChanges();

    dialog = fixture.nativeElement.querySelector(
      '[data-testid="log-weight-dialog"]',
    ) as HTMLDialogElement;

    const dialogMethods = installDialogMethods(dialog);
    showModal = dialogMethods.showModal;
    close = dialogMethods.close;
  });

  describe('URL date', () => {
    it('loads and renders the default date when the URL has no date', () => {
      const dateInput = fixture.nativeElement.querySelector(
        '[data-testid="date"]',
      ) as HTMLInputElement;

      expect(dateInput.value).toBe(defaultDate);
      expect(selectPresenterDate).toHaveBeenCalledOnce();
      expect(selectPresenterDate).toHaveBeenCalledWith(defaultDate);
    });

    it('loads and renders the date supplied by the URL', () => {
      queryParamMap.next(
        convertToParamMap({
          date: '2026-09-21',
        }),
      );
      fixture.detectChanges();

      const dateInput = fixture.nativeElement.querySelector(
        '[data-testid="date"]',
      ) as HTMLInputElement;

      expect(dateInput.value).toBe('2026-09-21');
      expect(selectPresenterDate).toHaveBeenCalledTimes(2);
      expect(selectPresenterDate).toHaveBeenLastCalledWith('2026-09-21');
    });

    it('writes the date entered by the user to the URL', () => {
      const dateInput = fixture.nativeElement.querySelector(
        '[data-testid="date"]',
      ) as HTMLInputElement;

      dateInput.value = '2026-09-21';
      dateInput.dispatchEvent(new Event('input'));

      expect(navigate).toHaveBeenCalledOnce();
      expect(navigate).toHaveBeenCalledWith([], {
        relativeTo: activatedRoute,
        queryParams: {
          date: '2026-09-21',
        },
        queryParamsHandling: 'merge',
        replaceUrl: true,
      });
    });
    it('waits for the current lookup before looking up a newer URL date', () => {
      const firstLookup = new Subject<void>();
      selectPresenterDate.mockClear();
      selectPresenterDate.mockReturnValueOnce(firstLookup);

      queryParamMap.next(convertToParamMap({ date: '2026-09-21' }));
      queryParamMap.next(convertToParamMap({ date: '2026-09-22' }));

      expect(selectPresenterDate).toHaveBeenCalledOnce();
      expect(selectPresenterDate).toHaveBeenLastCalledWith('2026-09-21');

      firstLookup.next(undefined);

      expect(selectPresenterDate).toHaveBeenCalledTimes(2);
      expect(selectPresenterDate).toHaveBeenLastCalledWith('2026-09-22');
    });
  });

  describe('dialog opening', () => {
    it('opens the dialog when logging is available', () => {
      dialog.returnValue = 'previous-result';

      const logButton = fixture.nativeElement.querySelector(
        '[data-testid="log"]',
      ) as HTMLButtonElement;

      logButton.click();

      expect(showModal).toHaveBeenCalledOnce();
      expect(dialog.returnValue).toBe('');
      expect(dialog.open).toBe(true);
    });

    it('disables the log action when the presenter reports it unavailable', () => {
      canEditWeight.set(false);
      fixture.detectChanges();

      const logButton = fixture.nativeElement.querySelector(
        '[data-testid="log"]',
      ) as HTMLButtonElement;

      expect(logButton.disabled).toBe(true);

      logButton.click();

      expect(showModal).not.toHaveBeenCalled();
    });

    it('does not open an already open dialog again', () => {
      const logButton = fixture.nativeElement.querySelector(
        '[data-testid="log"]',
      ) as HTMLButtonElement;

      logButton.click();
      logButton.click();

      expect(showModal).toHaveBeenCalledOnce();
    });
  });

  describe('dialog closure', () => {
    it('resets the form when the logged dialog closes', () => {
      dialog.returnValue = 'logged';

      dialog.dispatchEvent(new Event('close'));

      expect(resetForm).toHaveBeenCalledOnce();
      expect(cancelPresenterLog).not.toHaveBeenCalled();
    });

    it('cancels the presenter workflow for another closure result', () => {
      dialog.returnValue = 'canceled';

      dialog.dispatchEvent(new Event('close'));

      expect(cancelPresenterLog).toHaveBeenCalledOnce();
      expect(resetForm).not.toHaveBeenCalled();
    });

    it('closes the dialog when the user selects Cancel', () => {
      dialog.showModal();

      const cancelButton = dialog.querySelector('[data-testid="cancel-log"]') as HTMLButtonElement;

      cancelButton.click();

      expect(close).toHaveBeenCalledOnce();
      expect(close).toHaveBeenCalledWith('canceled');
    });
  });

  describe('weight form', () => {
    it('uses the measurement step supplied by the presenter', () => {
      const weightInput = dialog.querySelector('[data-testid="weight-input"]') as HTMLInputElement;

      expect(weightInput.step).toBe('0.1');
    });

    it('writes the entered weight to the presenter control', () => {
      dialog.showModal();

      const weightInput = dialog.querySelector('[data-testid="weight-input"]') as HTMLInputElement;

      weightInput.value = '72.5';
      weightInput.dispatchEvent(new Event('input'));

      expect(weightControl.value).toBe(72.5);
    });

    it('forwards submission and closes the dialog after the first successful result', () => {
      const result = new Subject<boolean>();
      log.mockReturnValue(result);
      dialog.showModal();

      const confirmButton = dialog.querySelector(
        '[data-testid="confirm-log"]',
      ) as HTMLButtonElement;

      confirmButton.click();
      result.next(true);
      result.next(true);

      expect(log).toHaveBeenCalledOnce();
      expect(close).toHaveBeenCalledOnce();
      expect(close).toHaveBeenCalledWith('logged');
    });

    it('keeps the dialog open when logging does not succeed', () => {
      log.mockReturnValue(of(false));
      dialog.showModal();

      const confirmButton = dialog.querySelector(
        '[data-testid="confirm-log"]',
      ) as HTMLButtonElement;

      confirmButton.click();

      expect(log).toHaveBeenCalledOnce();
      expect(close).not.toHaveBeenCalled();
      expect(dialog.open).toBe(true);
    });
  });

  describe('lookup retry', () => {
    it('retries the lookup for the currently selected date', () => {
      state.set({
        logState: { kind: 'idle' },
        getState: {
          kind: 'failure',
          title: 'Unable to get weight measurement',
          message: 'The measurement could not be loaded.',
        },
      });
      fixture.detectChanges();
      selectPresenterDate.mockClear();

      const retryButton = fixture.nativeElement.querySelector(
        '[data-testid="retry-get"]',
      ) as HTMLButtonElement;

      retryButton.click();

      expect(selectPresenterDate).toHaveBeenCalledOnce();
      expect(selectPresenterDate).toHaveBeenCalledWith(defaultDate);
    });
  });

  describe('rendered state', () => {
    it('renders the idle lookup state', () => {
      const status = fixture.nativeElement.querySelector(
        '[data-testid="lookup-idle"]',
      ) as HTMLElement | null;

      expect(status?.textContent).toContain('Preparing your journal');
    });

    it('hides workflow state when the presenter suppresses it', () => {
      showPageMainSection.set(false);
      fixture.detectChanges();

      const status = fixture.nativeElement.querySelector('[data-testid="lookup-idle"]');

      expect(status).toBeNull();
    });

    it('renders the lookup loading state while the date control is disabled', () => {
      dateValid.set(false);
      state.set({
        logState: { kind: 'idle' },
        getState: { kind: 'loading' },
      });
      fixture.detectChanges();

      const status = fixture.nativeElement.querySelector(
        '[data-testid="lookup-loading"]',
      ) as HTMLElement | null;

      expect(status?.textContent).toContain('Looking for a measurement');
      expect(status?.querySelector('app-shared-spinner')).not.toBeNull();
    });

    it('renders an existing measurement', () => {
      state.set({
        logState: { kind: 'idle' },
        getState: {
          kind: 'found',
          view: {
            date: defaultDate,
            weightInKg: 72.5,
          },
        },
      });
      fixture.detectChanges();

      const result = fixture.nativeElement.querySelector(
        '[data-testid="existing-measurement"]',
      ) as HTMLElement | null;

      expect(result?.textContent).toContain(defaultDate);
      expect(result?.textContent).toContain('72.5 kg');
    });

    it('renders the state in which no measurement exists', () => {
      state.set({
        logState: { kind: 'idle' },
        getState: {
          kind: 'not-found',
          title: 'No weight measurement',
          message: 'No measurement exists for this date.',
        },
      });
      fixture.detectChanges();

      const result = fixture.nativeElement.querySelector(
        '[data-testid="missing-measurement"]',
      ) as HTMLElement | null;

      expect(result?.textContent).toContain('No weight measurement');
      expect(result?.textContent).toContain('No measurement exists for this date.');
    });

    it('renders lookup failure details', () => {
      state.set({
        logState: { kind: 'idle' },
        getState: {
          kind: 'failure',
          title: 'Unable to get weight measurement',
          message: 'The measurement could not be loaded.',
        },
      });
      fixture.detectChanges();

      const failure = fixture.nativeElement.querySelector(
        '[data-testid="get-measurement-failure"]',
      ) as HTMLElement | null;

      expect(failure?.textContent).toContain('Unable to get weight measurement');
      expect(failure?.textContent).toContain('The measurement could not be loaded.');
    });

    it('renders the log loading state while the controls are disabled', () => {
      dateValid.set(false);
      state.set({
        logState: { kind: 'loading' },
        getState: {
          kind: 'not-found',
          title: 'No weight measurement',
          message: 'No measurement exists for this date.',
        },
      });
      fixture.detectChanges();

      const status = fixture.nativeElement.querySelector(
        '[data-testid="log-loading"]',
      ) as HTMLElement | null;

      expect(status?.textContent).toContain('Logging your measurement');
      expect(status?.querySelector('app-shared-spinner')).not.toBeNull();
      expect(fixture.nativeElement.querySelector('[data-testid="missing-measurement"]')).toBeNull();
    });

    it('renders the logged measurement', () => {
      state.set({
        logState: {
          kind: 'logged',
          view: {
            date: defaultDate,
            weightInKg: 72.5,
          },
        },
        getState: { kind: 'idle' },
      });
      fixture.detectChanges();

      const result = fixture.nativeElement.querySelector(
        '[data-testid="logged-measurement"]',
      ) as HTMLElement | null;

      expect(result?.textContent).toContain(defaultDate);
      expect(result?.textContent).toContain('72.5 kg');
    });

    it('renders logging failure details in the dialog', () => {
      dialog.showModal();
      state.set({
        logState: {
          kind: 'failure',
          title: 'Unable to log weight measurement',
          message: 'The weight measurement could not be logged.',
        },
        getState: {
          kind: 'not-found',
          title: 'No weight measurement',
          message: 'No measurement exists for this date.',
        },
      });
      fixture.detectChanges();

      const failure = dialog.querySelector(
        '[data-testid="log-measurement-failure"]',
      ) as HTMLElement | null;

      expect(failure?.textContent).toContain('Unable to log weight measurement');
      expect(failure?.textContent).toContain('The weight measurement could not be logged.');
    });
  });
});

function installDialogMethods(dialog: HTMLDialogElement): {
  readonly showModal: ReturnType<typeof vi.fn>;
  readonly close: ReturnType<typeof vi.fn>;
} {
  const showModal = vi.fn(() => {
    dialog.setAttribute('open', '');
  });

  const close = vi.fn((returnValue?: string) => {
    if (returnValue !== undefined) {
      dialog.returnValue = returnValue;
    }

    dialog.removeAttribute('open');
  });

  Object.defineProperties(dialog, {
    showModal: {
      configurable: true,
      value: showModal,
    },
    close: {
      configurable: true,
      value: close,
    },
  });

  return {
    showModal,
    close,
  };
}
