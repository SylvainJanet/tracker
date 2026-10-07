import { TestBed } from '@angular/core/testing';
import { describe, expect, it } from 'vitest';

import type { SharedRollingAverageGraphTooltipView } from '../../model/view/shared.graph.model.view';
import { SharedGraphRollingAverageTooltipPage } from './shared.graph.rolling-average-tooltip.page';

describe('SharedGraphRollingAverageTooltipPage', () => {
  it('renders a primary value and rolling averages with consistent coverage', async () => {
    const view: SharedRollingAverageGraphTooltipView = {
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
            includedValueCount: 7,
            windowInDays: 7,
          },
        },
        {
          seriesLabel: '14-day rolling average',
          color: '--color-analysis-weight-rolling-14',
          formattedValue: '82.03 kg',
          coverage: {
            includedValueCount: 9,
            windowInDays: 14,
          },
        },
      ],
    };

    await TestBed.configureTestingModule({
      imports: [SharedGraphRollingAverageTooltipPage],
    }).compileComponents();

    const fixture = TestBed.createComponent(SharedGraphRollingAverageTooltipPage);
    fixture.componentRef.setInput('view', view);
    fixture.detectChanges();

    const tooltip: HTMLElement | null = fixture.nativeElement.querySelector(
      '[data-testid="shared-graph-rolling-average-tooltip"]',
    );

    expect(tooltip).not.toBeNull();
    expect(normalizedText(tooltip)).toBe(
      '2026-09-23 Measured weight 81.9 kg Rolling averages ' +
        '7-day rolling average 82 kg Coverage: 7 of 7 days measured ' +
        '14-day rolling average 82.03 kg Coverage: 9 of 14 days measured',
    );

    const coverage = Array.from(tooltip?.querySelectorAll('small') ?? []).map(normalizedText);

    expect(coverage).toEqual(['Coverage: 7 of 7 days measured', 'Coverage: 9 of 14 days measured']);

    const markers = Array.from<HTMLElement>(
      tooltip?.querySelectorAll('[data-testid="graph-tooltip-series-marker"]') ?? [],
    );

    expect(markers.map((marker) => marker.style.getPropertyValue('--series-color'))).toEqual([
      'var(--color-action)',
      'var(--color-analysis-weight-rolling-7)',
      'var(--color-analysis-weight-rolling-14)',
    ]);
    expect(markers.every((marker) => marker.getAttribute('aria-hidden') === 'true')).toBe(true);
  });
});

function normalizedText(element: Element | null): string {
  return element?.textContent?.replaceAll(/\s+/g, ' ').trim() ?? '';
}
