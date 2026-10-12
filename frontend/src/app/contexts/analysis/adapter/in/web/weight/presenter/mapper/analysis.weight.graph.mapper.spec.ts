import { describe, expect, it } from 'vitest';

import type {
  AnalysisWeightMeasurementView,
  AnalysisWeightRollingAverageView,
} from '../../model/view/analysis.weight.model.view';
import { AnalysisWeightGraphMapper } from './analysis.weight.graph.mapper';

const measurements: readonly AnalysisWeightMeasurementView[] = [
  {
    date: '2026-09-20',
    dayNumber: 1,
    weightInKg: 82.1,
  },
];

const rollingAverages: readonly AnalysisWeightRollingAverageView[] = [
  {
    windowInDays: 7,
    points: [
      {
        date: '2026-09-20',
        dayNumber: 1,
        includedMeasurementCount: 1,
        averageWeightInKgApproximation: 82.1,
      },
      {
        date: '2026-09-21',
        dayNumber: 2,
        includedMeasurementCount: 1,
        averageWeightInKgApproximation: 82.1,
      },
    ],
  },
];

describe('AnalysisWeightGraphMapper', () => {
  it('maps measurements and rolling averages to graph series', () => {
    expect(AnalysisWeightGraphMapper.graphFor(measurements, rollingAverages)).toEqual({
      accessibleDescription:
        'Line graph of 1 measured weight and 1 rolling average on analysis day 1.',
      series: [
        {
          label: 'Measured weight',
          color: '--color-action',
          points: [{ x: 1, y: 82.1 }],
        },
        {
          label: '7-day rolling average',
          color: '--color-analysis-weight-rolling-7',
          points: [
            { x: 1, y: 82.1, intensity: 1 / 7 },
            { x: 2, y: 82.1, intensity: 1 / 7 },
          ],
        },
      ],
    });
  });

  it('maps tooltip data with and without a direct measurement', () => {
    expect(AnalysisWeightGraphMapper.tooltipByDayNumberFor(measurements, rollingAverages)).toEqual({
      1: {
        heading: '2026-09-20',
        primaryValue: {
          seriesLabel: 'Measured weight',
          color: '--color-action',
          formattedValue: '82.1 kg',
        },
        rollingAverages: [
          {
            seriesLabel: '7-day rolling average',
            color: '--color-analysis-weight-rolling-7',
            formattedValue: '82.1 kg',
            coverage: {
              includedValueCount: 1,
              windowInDays: 7,
            },
          },
        ],
      },
      2: {
        heading: '2026-09-21',
        primaryValue: {
          seriesLabel: 'Measured weight',
          color: '--color-action',
          formattedValue: 'No measurement',
        },
        rollingAverages: [
          {
            seriesLabel: '7-day rolling average',
            color: '--color-analysis-weight-rolling-7',
            formattedValue: '82.1 kg',
            coverage: {
              includedValueCount: 1,
              windowInDays: 7,
            },
          },
        ],
      },
    });
  });
});
