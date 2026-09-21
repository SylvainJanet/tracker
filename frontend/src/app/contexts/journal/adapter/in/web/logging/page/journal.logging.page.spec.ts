import { signal, type WritableSignal } from '@angular/core';
import { type ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, convertToParamMap, type ParamMap, Router } from '@angular/router';
import { BehaviorSubject, of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import type { JournalLoggingState } from '../model/journal.logging.model';
import {
  JOURNAL_LOGGING_PRESENTER_FACTORY,
  type JournalLoggingPresenter,
} from '../presenter/journal.logging.presenter';
import { JournalLoggingPage } from './journal.logging.page';

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
  let log: ReturnType<typeof vi.fn>;
  let resetForm: ReturnType<typeof vi.fn>;
  let cancelPresenterLog: ReturnType<typeof vi.fn>;
  let selectPresenterDate: ReturnType<typeof vi.fn>;

  let queryParamMap: BehaviorSubject<ParamMap>;
  let activatedRoute: {
    readonly queryParamMap: BehaviorSubject<ParamMap>;
  };
  let navigate: ReturnType<typeof vi.fn>;

  let dialog: HTMLDialogElement;
  let showModal: ReturnType<typeof vi.fn>;
  let close: ReturnType<typeof vi.fn>;

  beforeEach(async () => {
    state = signal<JournalLoggingState>({ kind: 'idle' });
    dateValid = signal(true);

    dateControl = new FormControl(defaultDate, {
      nonNullable: true,
    });
    weightControl = new FormControl<number | null>(null);
    weightForm = new FormGroup({
      weightInKg: weightControl,
    });

    log = vi.fn(() => of(false));
    selectPresenterDate = vi.fn((newDate: string) => {
      dateControl.setValue(newDate);
    });

    resetForm = vi.fn();
    cancelPresenterLog = vi.fn();

    const presenter = {
      state: state.asReadonly(),
      dateControl,
      weightControl,
      weightForm,

      get loading(): boolean {
        return state().kind === 'loading';
      },

      validDate: (): boolean => dateValid(),
      selectDate: selectPresenterDate,
      showWeightErrors: (): boolean => false,
      log,
      resetForm,
      cancelLog: cancelPresenterLog,
    } as unknown as JournalLoggingPresenter;

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
          useValue: (): JournalLoggingPresenter => presenter,
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

    dialog = fixture.nativeElement.querySelector('dialog') as HTMLDialogElement;

    const dialogMethods = installDialogMethods(dialog);
    showModal = dialogMethods.showModal;
    close = dialogMethods.close;
  });

  describe('URL date', () => {
    it('renders the default date when the URL has no date', () => {
      const dateInput = fixture.nativeElement.querySelector(
        '[data-testid="date"]',
      ) as HTMLInputElement;

      expect(dateInput.value).toBe(defaultDate);
      expect(selectPresenterDate).not.toHaveBeenCalled();
    });

    it('renders the date supplied by the URL', () => {
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
      expect(selectPresenterDate).toHaveBeenCalledOnce();
      expect(selectPresenterDate).toHaveBeenCalledWith('2026-09-21');
    });
  });

  describe('dialog opening', () => {
    it('opens the dialog when the user requests logging for a valid date', () => {
      dialog.returnValue = 'previous-result';
      const logButton = fixture.nativeElement.querySelector(
        '[data-testid="log"]',
      ) as HTMLButtonElement;

      logButton.click();

      expect(showModal).toHaveBeenCalledOnce();
      expect(dialog.returnValue).toBe('');
      expect(dialog.open).toBe(true);
    });

    it('disables the log action for an invalid date', () => {
      dateValid.set(false);
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
    it('writes the entered weight to the presenter control', () => {
      dialog.showModal();

      const weightInput = dialog.querySelector('[data-testid="weight-input"]') as HTMLInputElement;

      weightInput.value = '72.5';
      weightInput.dispatchEvent(new Event('input'));

      expect(weightControl.value).toBe(72.5);
    });

    it('forwards form submission to the presenter and closes after success', () => {
      log.mockReturnValue(of(true));
      dialog.showModal();

      const form = dialog.querySelector('form') as HTMLFormElement;

      form.dispatchEvent(new Event('submit'));

      expect(log).toHaveBeenCalledOnce();
      expect(close).toHaveBeenCalledOnce();
      expect(close).toHaveBeenCalledWith('logged');
    });

    it('keeps the dialog open when logging does not succeed', () => {
      log.mockReturnValue(of(false));
      dialog.showModal();

      const form = dialog.querySelector('form') as HTMLFormElement;

      form.dispatchEvent(new Event('submit'));

      expect(log).toHaveBeenCalledOnce();
      expect(close).not.toHaveBeenCalled();
      expect(dialog.open).toBe(true);
    });
  });

  describe('date selection', () => {
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
  });

  describe('rendered state', () => {
    it('renders no state-specific content while idle', () => {
      const stateElement = fixture.nativeElement.querySelector(
        '[aria-live="polite"], article, [role="alert"]',
      );

      expect(stateElement).toBeNull();
    });

    it('renders a loading message while logging', () => {
      state.set({ kind: 'loading' });
      fixture.detectChanges();

      const loadingElement = fixture.nativeElement.querySelector(
        '[aria-live="polite"]',
      ) as HTMLElement | null;

      expect(loadingElement?.textContent).toContain('Loading');
    });

    it('renders the logged measurement after success', () => {
      state.set({
        kind: 'log-successful',
        view: {
          date: defaultDate,
          weightInKg: 72.5,
        },
      });
      fixture.detectChanges();

      const resultElement = fixture.nativeElement.querySelector('article') as HTMLElement | null;

      expect(resultElement?.textContent).toContain(defaultDate);
      expect(resultElement?.textContent).toContain('72.5 kg');
    });

    it('renders the failure details in the open dialog', () => {
      dialog.showModal();
      state.set({
        kind: 'log-failure',
        problem: 'failure',
        title: 'Unable to log weight measurement',
        message: 'The weight measurement could not be logged.',
      });
      fixture.detectChanges();

      const failureElement = dialog.querySelector('[role="alert"]') as HTMLElement | null;

      expect(failureElement?.textContent).toContain('Unable to log weight measurement');
      expect(failureElement?.textContent).toContain('The weight measurement could not be logged.');
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
