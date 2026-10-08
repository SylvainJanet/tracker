import { Component, contentChild, input, signal, TemplateRef } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

import {
  SharedGraphView,
  type SharedRollingAverageGraphDialogView,
  type SharedRollingAverageGraphTooltipView,
} from '../../model/view/shared.graph.model.view';
import { SharedGraphPage } from '../../page/shared.graph.page';
import { SharedGraphRollingAverageDialogPage } from './shared.graph.rolling-average-dialog.page';
import { NgTemplateOutlet } from '@angular/common';
import { SharedGraphRollingAverageTooltipPage } from '../../rolling-average-tooltip/page/shared.graph.rolling-average-tooltip.page';

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
  `,
})
class SharedGraphPageStub {
  readonly graph = input.required<SharedGraphView>();
  readonly compact = input(false);
  readonly tooltipTemplate = contentChild<TemplateRef<{ readonly $implicit: number }>>(TemplateRef);
  readonly selectedX = signal<number | undefined>(undefined);

  showTooltipAt(x: number): void {
    this.selectedX.set(x);
  }
}

describe('SharedGraphRollingAverageDialogPage', () => {
  let originalShowModal: PropertyDescriptor | undefined;
  let originalClose: PropertyDescriptor | undefined;
  let showModal: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    originalShowModal = Object.getOwnPropertyDescriptor(HTMLDialogElement.prototype, 'showModal');
    originalClose = Object.getOwnPropertyDescriptor(HTMLDialogElement.prototype, 'close');

    showModal = vi.fn(function (this: HTMLDialogElement) {
      this.setAttribute('open', '');
    });

    Object.defineProperties(HTMLDialogElement.prototype, {
      showModal: {
        configurable: true,
        value: showModal,
      },
      close: {
        configurable: true,
        value: function (this: HTMLDialogElement) {
          this.removeAttribute('open');
          this.dispatchEvent(new Event('close'));
        },
      },
    });
  });

  afterEach(() => {
    restoreDialogMethod('showModal', originalShowModal);
    restoreDialogMethod('close', originalClose);
  });

  it('renders rolling details, a precise tooltip, and a compact trend graph', async () => {
    const trendGraph = new SharedGraphView('Compact weight trend.', [
      {
        label: 'Measured weight',
        color: '--color-action',
        points: [{ x: 4, y: 81.9 }],
      },
      {
        label: '7-day rolling average',
        color: '--color-analysis-weight-rolling-7',
        points: [{ x: 4, y: 81.9 }],
      },
    ]);
    const trendTooltip: SharedRollingAverageGraphTooltipView = {
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
          formattedValue: '81.9 kg',
          coverage: {
            includedValueCount: 3,
            windowInDays: 7,
          },
        },
      ],
    };
    const view: SharedRollingAverageGraphDialogView = {
      heading: '2026-09-23',
      primaryValue: {
        seriesLabel: 'Weight',
        color: '--color-action',
        formattedValue: '81.9 kg',
      },
      rollingAverages: [
        {
          seriesLabel: '7-day rolling average',
          color: '--color-analysis-weight-rolling-7',
          formattedValue: '81.9 kg',
          description: 'Average measured weight over the trailing 7 calendar days.',
          coverage: {
            includedValueCount: 3,
            windowInDays: 7,
          },
          calculation: {
            exactValue: {
              numerator: 245.8,
              denominator: 3,
            },
            prettyApproximation: '81.9 kg',
            preciseApproximation: '81.933333 kg',
          },
          trend: {
            graph: trendGraph,
            tooltipByX: {
              4: trendTooltip,
            },
          },
        },
      ],
    };

    await TestBed.configureTestingModule({
      imports: [SharedGraphRollingAverageDialogPage],
    })
      .overrideComponent(SharedGraphRollingAverageDialogPage, {
        remove: {
          imports: [SharedGraphPage],
        },
        add: {
          imports: [SharedGraphPageStub],
        },
      })
      .compileComponents();

    const fixture = TestBed.createComponent(SharedGraphRollingAverageDialogPage);
    const closed = vi.fn();

    fixture.componentInstance.closed.subscribe(closed);
    fixture.componentRef.setInput('view', view);
    fixture.detectChanges();

    const dialog = fixture.nativeElement.querySelector(
      '[data-testid="shared-graph-rolling-average-dialog"]',
    ) as HTMLDialogElement;
    const title = dialog.querySelector('[data-testid="graph-dialog-title"]');
    const primaryValue = dialog.querySelector('[data-testid="graph-dialog-primary-value"]');
    const rollingAverage = dialog.querySelector('[data-testid="graph-dialog-rolling-average"]');
    const prettyApproximation = dialog.querySelector(
      '[data-testid="graph-dialog-pretty-average"]',
    ) as HTMLElement;
    const preciseTooltip = dialog.querySelector(
      '[data-testid="graph-dialog-precise-average-tooltip"]',
    ) as HTMLElement;

    expect(showModal).toHaveBeenCalledOnce();
    expect(dialog.open).toBe(true);
    expect(dialog.getAttribute('closedby')).toBe('any');
    expect(normalizedText(title)).toBe('2026-09-23 — Weight: 81.9 kg');
    expect(normalizedText(primaryValue)).toBe('Weight 81.9 kg');
    expect(normalizedText(rollingAverage)).toContain('7-day rolling average 81.9 kg');
    expect(normalizedText(rollingAverage)).toContain(
      'Average measured weight over the trailing 7 calendar days.',
    );
    expect(normalizedText(rollingAverage)).toContain('Coverage: 3 of 7 days measured');
    expect(normalizedText(rollingAverage)).toContain('Average = 245.8 / 3 ≈ 81.9 kg');

    expect(prettyApproximation.getAttribute('tabindex')).toBe('0');
    expect(prettyApproximation.getAttribute('aria-describedby')).toBe(preciseTooltip.id);
    expect(preciseTooltip.getAttribute('role')).toBe('tooltip');
    expect(normalizedText(preciseTooltip)).toBe('Precise average: 81.933333 kg');

    const markers = Array.from<HTMLElement>(
      dialog.querySelectorAll('[data-testid="graph-dialog-series-marker"]'),
    );

    expect(markers.map((marker) => marker.style.getPropertyValue('--series-color'))).toEqual([
      'var(--color-action)',
      'var(--color-analysis-weight-rolling-7)',
    ]);

    const graphElement = fixture.debugElement.query(By.directive(SharedGraphPageStub));
    const graphPage = graphElement.injector.get(SharedGraphPageStub);

    expect(graphPage.graph()).toBe(trendGraph);
    expect(graphPage.compact()).toBe(true);

    graphPage.showTooltipAt(4);
    fixture.detectChanges();

    const tooltipElement = fixture.debugElement.query(
      By.directive(SharedGraphRollingAverageTooltipPage),
    );
    const tooltipPage = tooltipElement?.injector.get(SharedGraphRollingAverageTooltipPage);

    expect(tooltipElement).not.toBeNull();
    expect(tooltipPage?.view()).toBe(trendTooltip);

    const closeButton = dialog.querySelector(
      '[data-testid="close-graph-dialog"]',
    ) as HTMLButtonElement;

    closeButton.click();

    expect(closed).toHaveBeenCalledOnce();
    expect(dialog.open).toBe(false);
  });
});

function restoreDialogMethod(
  name: 'showModal' | 'close',
  descriptor: PropertyDescriptor | undefined,
): void {
  if (descriptor === undefined) {
    Reflect.deleteProperty(HTMLDialogElement.prototype, name);
    return;
  }

  Object.defineProperty(HTMLDialogElement.prototype, name, descriptor);
}

function normalizedText(element: Element | null): string {
  return element?.textContent?.replaceAll(/\s+/g, ' ').trim() ?? '';
}
