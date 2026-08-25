import { signal } from '@angular/core';
import { type ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter, Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { BehaviorSubject } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { type DailyRecordSummaryReadModel } from '../model/daily-record.summary.model';
import { DAILY_RECORD_SUMMARY_PRESENTER_FACTORY } from '../presenter/daily-record.summary.presenter';
import { DailyRecordSummaryPage } from './daily-record.summary.page';

describe('DailyRecordSummaryPage', () => {
  const model = signal<DailyRecordSummaryReadModel>({
    selectedDate: '2026-08-27',
    loading: false,
    state: {
      kind: 'idle',
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
  let page: ComponentFixture<DailyRecordSummaryPage>;

  beforeEach(async () => {
    vi.restoreAllMocks();
    vi.clearAllMocks();

    queryParamMap.next(convertToParamMap({}));

    model.set({
      selectedDate: '2026-08-27',
      loading: false,
      state: {
        kind: 'idle',
      },
    });

    await TestBed.configureTestingModule({
      imports: [DailyRecordSummaryPage],
      providers: [
        provideRouter([]),
        {
          provide: DAILY_RECORD_SUMMARY_PRESENTER_FACTORY,
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

    page = TestBed.createComponent(DailyRecordSummaryPage);
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

    page = TestBed.createComponent(DailyRecordSummaryPage);
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
      loading: false,
      state: {
        kind: 'view',
        view: {
          date: '2026-08-27',
          status: 'In progress',
        },
      },
    });

    page.detectChanges();

    const record = page.nativeElement.querySelector('[data-testid="summary-record"]');

    expect(record).not.toBeNull();
    expect(record.textContent).toContain('2026-08-27');
    expect(record.textContent).toContain('In progress');
    expect(record.textContent).not.toContain('IN_PROGRESS');
  });

  it('renders loading state and disables date selection', () => {
    model.set({
      selectedDate: '2026-08-27',
      loading: true,
      state: { kind: 'loading' },
    });

    page.detectChanges();

    const dateInput = page.nativeElement.querySelector(
      '[data-testid="summary-date"]',
    ) as HTMLInputElement;

    expect(page.nativeElement.querySelector('[aria-live="polite"]')?.textContent).toContain(
      'Loading',
    );
    expect(dateInput.disabled).toBe(true);
  });

  it.each(['daily-record', 'records'])(
    'offers to log the selected date through a sibling route under the /%s mount',
    async (contextPath) => {
      page.destroy();
      vi.mocked(router.navigate).mockRestore();

      router.resetConfig([
        {
          path: contextPath,
          children: [
            {
              path: 'summary',
              component: DailyRecordSummaryPage,
            },
          ],
        },
      ]);

      model.set({
        selectedDate: '2026-08-27',
        loading: false,
        state: {
          kind: 'problem',
          problem: 'not-found',
          title: 'Daily record not found',
          message: 'No daily record exists for 2026-08-27.',
        },
      });

      const harness = await RouterTestingHarness.create(`/${contextPath}/summary?date=2026-08-27`);
      const renderedPage = harness.routeNativeElement;

      const alert = renderedPage?.querySelector('[role="alert"]');
      const loggingLink = renderedPage?.querySelector(
        '[data-testid="log-missing-record"]',
      ) as HTMLAnchorElement | null;

      expect(alert?.textContent).toContain('Daily record not found');
      expect(loggingLink?.textContent).toContain('Log this day');
      expect(loggingLink?.getAttribute('href')).toBe(
        `/${contextPath}/daily-logging?date=2026-08-27`,
      );
    },
  );
});
