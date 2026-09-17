import { signal } from '@angular/core';
import { type ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter, Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { type DailyRecordSummaryReadModel } from '../model/journal.summary.model';
import { JournalSummaryPage } from './journal.summary.page';
import { JOURNAL_SUMMARY_PRESENTER_FACTORY } from '../presenter/journal.summary.presenter';

describe('DailyRecordSummaryPage', () => {
  const model = signal<DailyRecordSummaryReadModel>({
    selectedDate: '2026-08-27',
    state: {
      kind: 'view',
      view: {
        date: '2026-08-27',
      },
    },
  });

  const presenter = {
    model: model.asReadonly(),

    selectDate: vi.fn(async () => {
      // Do nothing.
    }),
  };

  const queryParamMap = new BehaviorSubject(convertToParamMap({}));

  let activatedRoute: ActivatedRoute;
  let router: Router;
  let page: ComponentFixture<JournalSummaryPage>;

  beforeEach(async () => {
    vi.restoreAllMocks();
    vi.clearAllMocks();

    queryParamMap.next(convertToParamMap({}));

    model.set({
      selectedDate: '2026-08-27',
      state: {
        kind: 'view',
        view: {
          date: '2026-08-27',
        },
      },
    });

    await TestBed.configureTestingModule({
      imports: [JournalSummaryPage],
      providers: [
        provideRouter([]),
        {
          provide: JOURNAL_SUMMARY_PRESENTER_FACTORY,
          useValue: () => presenter,
        },
      ],
    }).compileComponents();

    activatedRoute = TestBed.inject(ActivatedRoute);

    Object.defineProperty(activatedRoute, 'queryParamMap', {
      configurable: true,
      value: queryParamMap,
    });

    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);

    page = TestBed.createComponent(JournalSummaryPage);
    page.detectChanges();
  });

  it('loads the initial selected date when the URL has no date', () => {
    expect(presenter.selectDate).toHaveBeenCalledOnce();
    expect(presenter.selectDate).toHaveBeenCalledWith('2026-08-27');
  });

  it('loads the date provided by the URL when the page initializes', () => {
    page.destroy();

    queryParamMap.next(
      convertToParamMap({
        date: '2026-08-29',
      }),
    );

    vi.clearAllMocks();

    page = TestBed.createComponent(JournalSummaryPage);
    page.detectChanges();

    expect(presenter.selectDate).toHaveBeenCalledOnce();
    expect(presenter.selectDate).toHaveBeenCalledWith('2026-08-29');
  });

  it('forwards URL date changes to the presenter', () => {
    vi.clearAllMocks();

    queryParamMap.next(
      convertToParamMap({
        date: '2026-08-28',
      }),
    );

    expect(presenter.selectDate).toHaveBeenCalledOnce();
    expect(presenter.selectDate).toHaveBeenCalledWith('2026-08-28');
  });

  it('writes date changes to the URL', () => {
    vi.clearAllMocks();

    const input = page.nativeElement.querySelector(
      '[data-testid="summary-date"]',
    ) as HTMLInputElement;

    input.value = '2026-08-28';
    input.dispatchEvent(new Event('change'));

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

  it('renders an existing daily record', () => {
    model.set({
      selectedDate: '2026-08-27',
      state: {
        kind: 'view',
        view: {
          date: '2026-08-27',
        },
      },
    });

    page.detectChanges();

    const record = page.nativeElement.querySelector('[data-testid="summary-record"]');

    expect(record).not.toBeNull();
    expect(record.textContent).toContain('2026-08-27');
  });
});
