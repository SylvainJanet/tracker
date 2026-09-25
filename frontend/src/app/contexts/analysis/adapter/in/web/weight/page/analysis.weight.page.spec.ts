import { signal, type WritableSignal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import type { AnalysisWeightState } from '../model/state/analysis.weight.model.state';
import {
  ANALYSIS_WEIGHT_PRESENTER_FACTORY,
  type AnalysisWeightPresenter,
} from '../presenter/analysis.weight.presenter';
import { AnalysisWeightPage } from './analysis.weight.page';

describe('AnalysisWeightPage', () => {
  let currentState: WritableSignal<AnalysisWeightState>;
  let analyze: ReturnType<typeof vi.fn<AnalysisWeightPresenter['analyze']>>;
  let presenter: AnalysisWeightPresenter;

  beforeEach(async () => {
    currentState = signal<AnalysisWeightState>({
      analysisState: { kind: 'loading' },
    });
    analyze = vi.fn<AnalysisWeightPresenter['analyze']>(() => of());
    presenter = {
      state: currentState.asReadonly(),
      analyze,
    } as unknown as AnalysisWeightPresenter;

    await TestBed.configureTestingModule({
      imports: [AnalysisWeightPage],
      providers: [
        {
          provide: ANALYSIS_WEIGHT_PRESENTER_FACTORY,
          useValue: () => presenter,
        },
      ],
    }).compileComponents();
  });

  it('presents an accessible loading state', () => {
    const fixture = TestBed.createComponent(AnalysisWeightPage);

    fixture.detectChanges();

    const loading = fixture.nativeElement.querySelector(
      '[data-testid="loading-weight-analysis"]',
    ) as HTMLElement;

    expect(loading).not.toBeNull();
    expect(loading.getAttribute('aria-live')).toBe('polite');
    expect(loading.querySelector('app-shared-spinner')).not.toBeNull();
    expect(normalizedText(loading)).toContain('Loading weight analysis');
  });

  it('renders the analysis summary and measurements as semantic data', () => {
    currentState.set({
      analysisState: {
        kind: 'analyzed',
        view: {
          timelineStartDate: '2026-09-20',
          range: {
            startDate: '2026-09-20',
            endDate: '2026-09-25',
          },
          weightMeasurements: [
            {
              date: '2026-09-20',
              dayNumber: 1,
              weightInKg: 82.1,
            },
            {
              date: '2026-09-23',
              dayNumber: 4,
              weightInKg: 81.9,
            },
          ],
        },
      },
    });

    const fixture = TestBed.createComponent(AnalysisWeightPage);
    fixture.detectChanges();

    const summary = fixture.nativeElement.querySelector(
      '[data-testid="weight-analysis-summary"]',
    ) as HTMLElement;
    const table = fixture.nativeElement.querySelector('table') as HTMLTableElement;

    expect(Array.from(summary.querySelectorAll('dt'), (term) => normalizedText(term))).toEqual([
      'Timeline begins',
      'Analysis period',
      'Measurements',
    ]);

    expect(
      Array.from(summary.querySelectorAll('time'), (time) => time.getAttribute('datetime')),
    ).toEqual(['2026-09-20', '2026-09-20', '2026-09-25']);

    expect(
      Array.from(summary.querySelectorAll('dd'), (description) => normalizedText(description))[2],
    ).toBe('2');

    expect(
      Array.from(table.querySelectorAll('thead th'), (heading) => normalizedText(heading)),
    ).toEqual(['Day', 'Date', 'Weight']);

    expect(
      Array.from(table.tBodies[0]?.rows ?? [], (row) =>
        Array.from(row.cells, (cell) => normalizedText(cell)),
      ),
    ).toEqual([
      ['1', '2026-09-20', '82.1 kg'],
      ['4', '2026-09-23', '81.9 kg'],
    ]);

    expect(
      Array.from(table.querySelectorAll('tbody time'), (time) => time.getAttribute('datetime')),
    ).toEqual(['2026-09-20', '2026-09-23']);
  });

  it('renders the empty-state title and message', () => {
    currentState.set({
      analysisState: {
        kind: 'empty',
        title: 'No weight measurement found',
        message: 'Nothing to display: no weight measurement was found.',
      },
    });

    const fixture = TestBed.createComponent(AnalysisWeightPage);
    fixture.detectChanges();

    const emptyState = fixture.nativeElement.querySelector(
      '[data-testid="empty-weight-analysis"]',
    ) as HTMLElement;

    expect(emptyState.querySelector('h2')?.textContent?.trim()).toBe('No weight measurement found');
    expect(normalizedText(emptyState)).toContain(
      'Nothing to display: no weight measurement was found.',
    );
  });

  it('renders an accessible failure with a retry action', () => {
    currentState.set({
      analysisState: {
        kind: 'failure',
        title: 'Unable to perform weight analysis',
        message: 'The weight analysis could not be performed: backend unavailable',
      },
    });

    const fixture = TestBed.createComponent(AnalysisWeightPage);
    fixture.detectChanges();

    const alert = fixture.nativeElement.querySelector('[role="alert"]') as HTMLElement;
    const retry = alert.querySelector('[data-testid="retry-analysis"]') as HTMLButtonElement;

    expect(alert.querySelector('h2')?.textContent?.trim()).toBe(
      'Unable to perform weight analysis',
    );
    expect(normalizedText(alert)).toContain(
      'The weight analysis could not be performed: backend unavailable',
    );
    expect(retry.textContent?.trim()).toBe('Try again');
  });

  it('forwards retry to the presenter', () => {
    currentState.set({
      analysisState: {
        kind: 'failure',
        title: 'Unable to perform weight analysis',
        message: 'The weight analysis could not be performed: backend unavailable',
      },
    });

    const fixture = TestBed.createComponent(AnalysisWeightPage);
    fixture.detectChanges();

    const retry = fixture.nativeElement.querySelector(
      '[data-testid="retry-analysis"]',
    ) as HTMLButtonElement;

    retry.click();

    expect(analyze).toHaveBeenCalledOnce();
  });
});

function normalizedText(element: Element): string {
  return element.textContent?.replace(/\s+/g, ' ').trim() ?? '';
}
