import { signal } from '@angular/core';
import { type ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { JournalLoggingModel } from '../model/journal.logging.model';
import { JOURNAL_LOGGING_PRESENTER_FACTORY } from '../presenter/journal.logging.presenter';
import { JournalLoggingPage } from './journal.logging.page';
import { calendarDate } from '../../../../../domain/calendar-date';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

describe('JournalLoggingPage', () => {
  const model = signal(JournalLoggingModel.initial('2026-08-27'));

  const presenter = {
    model: model.asReadonly(),

    selectDate: vi.fn(),

    create: vi.fn(async () => {
      // do nothing
    }),
  };

  const queryParamMap = new BehaviorSubject(convertToParamMap({}));

  const activatedRoute = {
    queryParamMap,
  };

  const router = {
    navigate: vi.fn(async () => true),
  };

  let page: ComponentFixture<JournalLoggingPage>;

  beforeEach(async () => {
    queryParamMap.next(convertToParamMap({}));
    vi.clearAllMocks();

    model.set(JournalLoggingModel.initial('2026-08-27'));

    await TestBed.configureTestingModule({
      imports: [JournalLoggingPage],
      providers: [
        {
          provide: JOURNAL_LOGGING_PRESENTER_FACTORY,
          useValue: () => presenter,
        },

        {
          provide: ActivatedRoute,
          useValue: activatedRoute,
        },
        {
          provide: Router,
          useValue: router,
        },
      ],
    }).compileComponents();

    page = TestBed.createComponent(JournalLoggingPage);

    page.detectChanges();
  });

  it('keeps the presenter default when the URL has no date', () => {
    expect(presenter.selectDate).not.toHaveBeenCalled();
    expect(presenter.model().selectedDate).toBe('2026-08-27');
  });

  it('forwards URL date changes to the presenter', () => {
    vi.clearAllMocks();

    queryParamMap.next(
      convertToParamMap({
        date: '2026-08-29',
      }),
    );

    expect(presenter.selectDate).toHaveBeenCalledOnce();
    expect(presenter.selectDate).toHaveBeenCalledWith('2026-08-29');
  });

  it('writes date changes to the URL', () => {
    const input = page.nativeElement.querySelector('[data-testid="date"]') as HTMLInputElement;

    input.value = '2026-08-28';
    input.dispatchEvent(new Event('input'));

    expect(router.navigate).toHaveBeenCalledWith([], {
      relativeTo: activatedRoute,
      queryParams: {
        date: '2026-08-28',
      },
      queryParamsHandling: 'merge',
      replaceUrl: true,
    });
    expect(presenter.selectDate).not.toHaveBeenCalled();
  });

  it('opens a modal confirmation before creating a daily record', () => {
    const dialog = page.nativeElement.querySelector('dialog') as HTMLDialogElement;

    expect(dialog.getAttribute('closedby')).toBe('any');

    const { showModal, close } = installDialogMethods(dialog);

    const createButton = page.nativeElement.querySelector(
      '[data-testid="create"]',
    ) as HTMLButtonElement;

    model.update((model) => model.selectWeight(1234));
    page.detectChanges();

    createButton.click();
    page.detectChanges();

    expect(showModal).toHaveBeenCalledOnce();
    expect(dialog.open).toBe(true);
    expect(presenter.create).not.toHaveBeenCalled();
    expect(dialog.textContent).toContain('Log this day?');
    expect(dialog.textContent).toContain('2026-08-27');

    const confirmButton = page.nativeElement.querySelector(
      '[data-testid="confirm-create"]',
    ) as HTMLButtonElement;

    confirmButton.click();

    expect(close).toHaveBeenCalledOnce();
    expect(presenter.create).toHaveBeenCalledOnce();
  });

  it('cancels creation without invoking the presenter', () => {
    const dialog = page.nativeElement.querySelector('dialog') as HTMLDialogElement;
    const { close } = installDialogMethods(dialog);

    const createButton = page.nativeElement.querySelector(
      '[data-testid="create"]',
    ) as HTMLButtonElement;
    const cancelButton = dialog.querySelector(
      '.confirmation-actions button:not([data-testid])',
    ) as HTMLButtonElement;

    createButton.click();
    cancelButton.click();

    expect(close).toHaveBeenCalledOnce();
    expect(dialog.open).toBe(false);
    expect(presenter.create).not.toHaveBeenCalled();
  });

  it('renders the presenter record state', () => {
    const date = calendarDate('2026-08-27');

    model.set(
      new JournalLoggingModel('2026-08-27', 1234, {
        kind: 'view',
        view: {
          date,
          weight: 1234,
        },
      }),
    );

    page.detectChanges();

    expect(page.nativeElement.textContent).toContain('2026-08-27');

    expect(page.nativeElement.textContent).toContain('1234');
  });

  it('renders loading state and disables its controls', () => {
    model.set(new JournalLoggingModel('2026-08-27', 1234, { kind: 'loading' }));

    page.detectChanges();

    const dateInput = page.nativeElement.querySelector('[data-testid="date"]') as HTMLInputElement;
    const createButton = page.nativeElement.querySelector(
      '[data-testid="create"]',
    ) as HTMLButtonElement;

    expect(page.nativeElement.querySelector('[aria-live="polite"]')?.textContent).toContain(
      'Loading',
    );
    expect(dateInput.disabled).toBe(true);
    expect(createButton.disabled).toBe(true);
  });

  it('renders the presenter problem state', () => {
    model.set(
      new JournalLoggingModel('2026-08-27', 1234, {
        kind: 'problem',
        problem: 'already-exists',
        title: 'Daily record already exists',
        message: 'A daily record already exists for 2026-08-27.',
      }),
    );

    page.detectChanges();

    const alert = page.nativeElement.querySelector('[role="alert"]');

    expect(alert.textContent).toContain('Daily record already exists');

    expect(alert.textContent).toContain('A daily record already exists for 2026-08-27.');
  });

  it('selects the date provided by the URL when the page initializes', () => {
    page.destroy();

    queryParamMap.next(
      convertToParamMap({
        date: '2026-08-29',
      }),
    );

    vi.clearAllMocks();

    page = TestBed.createComponent(JournalLoggingPage);
    page.detectChanges();

    expect(presenter.selectDate).toHaveBeenCalledOnce();
    expect(presenter.selectDate).toHaveBeenCalledWith('2026-08-29');
  });
});

function installDialogMethods(dialog: HTMLDialogElement): {
  readonly showModal: ReturnType<typeof vi.fn>;
  readonly close: ReturnType<typeof vi.fn>;
} {
  const showModal = vi.fn(() => dialog.setAttribute('open', ''));
  const close = vi.fn(() => dialog.removeAttribute('open'));

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

  return { showModal, close };
}
