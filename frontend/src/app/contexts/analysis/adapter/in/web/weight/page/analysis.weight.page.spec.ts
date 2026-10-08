import { NgTemplateOutlet } from '@angular/common';
import {
  Component,
  contentChild,
  input,
  output,
  signal,
  TemplateRef,
  type WritableSignal,
} from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import {
  SharedGraphPage,
  SharedGraphRollingAverageTooltipPage,
  SharedGraphView,
  type SharedRollingAverageGraphTooltipView,
  SharedGraphRollingAverageDialogPage,
  type SharedRollingAverageGraphDialogView,
} from '../../../../../../../shared/api/shared.graph';
import type { AnalysisWeightState } from '../model/state/analysis.weight.model.state';
import {
  ANALYSIS_WEIGHT_PRESENTER_FACTORY,
  type AnalysisWeightPresenter,
} from '../presenter/analysis.weight.presenter';
import { AnalysisWeightPage } from './analysis.weight.page';

interface SharedGraphNodeDetailsContext {
  readonly $implicit: number;
  readonly close: () => void;
}

@Component({
  selector: 'app-shared-graph',
  imports: [NgTemplateOutlet],
  template: `
    @if (tooltipTemplate(); as template) {
      @if (selectedX(); as x) {
        <ng-container
          [ngTemplateOutlet]="template"
          [ngTemplateOutletContext]="{ $implicit: x }"
        ></ng-container>
      }
    }

    @if (graphNodeDetailsTemplate(); as template) {
      @if (selectedNodeDetails(); as details) {
        <ng-container
          [ngTemplateOutlet]="template"
          [ngTemplateOutletContext]="{
            $implicit: details.x,
            close: closeNodeDetails,
          }"
        ></ng-container>
      }
    }
  `,
})
class SharedGraphPageStub {
  readonly graph = input.required<SharedGraphView>();
  readonly tooltipTemplate = contentChild<TemplateRef<{ readonly $implicit: number }>>(TemplateRef);
  readonly graphNodeDetailsTemplate =
    contentChild<TemplateRef<SharedGraphNodeDetailsContext>>('graphNodeDetails');

  readonly selectedX = signal<number | undefined>(undefined);
  readonly selectedNodeDetails = signal<{ readonly x: number } | undefined>(undefined);

  readonly closeNodeDetails = (): void => {
    this.selectedNodeDetails.set(undefined);
  };

  showTooltipAt(x: number): void {
    this.selectedX.set(x);
  }

  showDetailsAt(x: number): void {
    this.selectedNodeDetails.set({ x });
  }
}

@Component({
  selector: 'app-shared-graph-rolling-average-dialog',
  template: '',
})
class SharedGraphRollingAverageDialogPageStub {
  readonly view = input.required<SharedRollingAverageGraphDialogView>();
  readonly closed = output<void>();

  close(): void {
    this.closed.emit();
  }
}

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
    })
      .overrideComponent(AnalysisWeightPage, {
        remove: {
          imports: [SharedGraphPage, SharedGraphRollingAverageDialogPage],
        },
        add: {
          imports: [SharedGraphPageStub, SharedGraphRollingAverageDialogPageStub],
        },
      })
      .compileComponents();
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
    const graph = new SharedGraphView(
      'Line graph of 2 measured weights from analysis day 1 to analysis day 4.',
      [
        {
          label: 'Measured weight',
          color: '--color-action',
          points: [
            { x: 1, y: 82.1 },
            { x: 4, y: 81.9 },
          ],
        },
        {
          label: '7-day rolling average',
          color: '--color-analysis-weight-rolling-7',
          points: [{ x: 4, y: 82, intensity: 2 / 7 }],
        },
      ],
    );
    const tooltipView: SharedRollingAverageGraphTooltipView = {
      heading: '2026-09-23',
      primaryValue: {
        seriesLabel: 'Measured weight',
        color: '--color-action',
        formattedValue: '81.9 kg',
      },
      rollingAverages: [
        {
          seriesLabel: '7-day rolling average',
          color: '--color-analysis-weight-rolling-7',
          formattedValue: '82 kg',
          coverage: {
            includedValueCount: 2,
            windowInDays: 7,
          },
        },
      ],
    };
    const dialogView: SharedRollingAverageGraphDialogView = {
      heading: '2026-09-23',
      primaryValue: {
        seriesLabel: 'Weight',
        color: '--color-action',
        formattedValue: '81.9 kg',
      },
      rollingAverages: [],
    };

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
          rollingAverages: [
            {
              windowInDays: 7,
              points: [
                {
                  date: '2026-09-23',
                  dayNumber: 4,
                  includedMeasurementCount: 2,
                  averageWeightInKgApproximation: 82,
                  completeCalendarWindow: false,
                },
              ],
            },
          ],
          graphTooltipByDayNumber: {
            4: tooltipView,
          },
          graphDialogByDayNumber: {
            4: dialogView,
          },
          graph,
        },
      },
    });

    const fixture = TestBed.createComponent(AnalysisWeightPage);
    fixture.detectChanges();

    const graphElement = fixture.debugElement.query(By.directive(SharedGraphPageStub));
    const graphPage = graphElement?.injector.get(SharedGraphPageStub);

    expect(graphElement).not.toBeNull();
    expect(graphPage?.graph()).toBe(graph);

    graphPage?.showTooltipAt(4);
    fixture.detectChanges();

    const tooltipElement = fixture.debugElement.query(
      By.directive(SharedGraphRollingAverageTooltipPage),
    );
    const tooltipPage = tooltipElement?.injector.get(SharedGraphRollingAverageTooltipPage);

    expect(tooltipElement).not.toBeNull();
    expect(tooltipPage?.view()).toBe(tooltipView);

    graphPage?.showDetailsAt(4);
    fixture.detectChanges();

    const dialogElement = fixture.debugElement.query(
      By.directive(SharedGraphRollingAverageDialogPageStub),
    );
    const dialogPage = dialogElement?.injector.get(SharedGraphRollingAverageDialogPageStub);

    expect(dialogElement).not.toBeNull();
    expect(dialogPage?.view()).toBe(dialogView);

    dialogPage?.close();
    fixture.detectChanges();

    expect(
      fixture.debugElement.query(By.directive(SharedGraphRollingAverageDialogPageStub)),
    ).toBeNull();

    const summary = fixture.nativeElement.querySelector(
      '[data-testid="weight-analysis-summary"]',
    ) as HTMLElement;
    const measurementTable = fixture.nativeElement.querySelector(
      'section[aria-labelledby="weight-measurements-caption"] table',
    ) as HTMLTableElement;

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
      Array.from(measurementTable.querySelectorAll('thead th'), (heading) =>
        normalizedText(heading),
      ),
    ).toEqual(['Day', 'Date', 'Weight']);

    expect(
      Array.from(measurementTable.tBodies[0]?.rows ?? [], (row) =>
        Array.from(row.cells, (cell) => normalizedText(cell)),
      ),
    ).toEqual([
      ['1', '2026-09-20', '82.1 kg'],
      ['4', '2026-09-23', '81.9 kg'],
    ]);

    expect(
      Array.from(measurementTable.querySelectorAll('tbody time'), (time) =>
        time.getAttribute('datetime'),
      ),
    ).toEqual(['2026-09-20', '2026-09-23']);

    const rollingTable = fixture.nativeElement.querySelector(
      '[data-testid="weight-rolling-averages"]',
    ) as HTMLTableElement;

    expect(rollingTable).not.toBeNull();

    expect(
      Array.from(rollingTable.querySelectorAll('thead th'), (heading) => normalizedText(heading)),
    ).toEqual(['Window', 'Day', 'Date', 'Average', 'Coverage', 'Calendar window']);

    expect(
      Array.from(rollingTable.tBodies[0]?.rows ?? [], (row) =>
        Array.from(row.cells, (cell) => normalizedText(cell)),
      ),
    ).toEqual([['7 days', '4', '2026-09-23', '82 kg', '2 of 7 days', 'Partial']]);

    expect(normalizedText(rollingTable)).not.toContain('164.00');

    const tableRegions = Array.from(
      fixture.nativeElement.querySelectorAll(
        'section.analysis-table-region',
      ) as NodeListOf<HTMLElement>,
    );

    expect(tableRegions.map((region) => region.getAttribute('aria-labelledby'))).toEqual([
      'weight-rolling-averages-caption',
      'weight-measurements-caption',
    ]);
    expect(tableRegions.every((region) => !region.hasAttribute('role'))).toBe(true);
    expect(tableRegions.every((region) => !region.hasAttribute('tabindex'))).toBe(true);
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

    expect(alert.tagName).toBe('DIV');
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
