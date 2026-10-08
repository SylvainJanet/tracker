import { describe, expect, it } from 'vitest';

import type { WeightAnalysisResultData } from '../../../../../../application/port/in/get-weight-analysis.use-case';
import { AnalysisWeightMapper } from './analysis.weight.mapper';

describe('AnalysisWeightMapper', () => {
  it('maps weight analysis result data to a presentation view', () => {
    const resultData: WeightAnalysisResultData = {
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
      rollingAverageSeries: [
        {
          windowSize: 7,
          rollingAverages: [
            {
              date: '2026-09-20',
              dayNumber: 1,
              includedValues: [
                {
                  date: '2026-09-20',
                  dayNumber: 1,
                  weightInKg: 82.1,
                },
              ],
              rollingAverage: {
                exactValue: {
                  numerator: 82.1,
                  denominator: 1,
                },
                approximations: [
                  { value: 82.1, rounding: 'PRETTY' },
                  { value: 82.1, rounding: 'PRECISE' },
                ],
              },
            },
            {
              date: '2026-09-23',
              dayNumber: 4,
              includedValues: [
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
              rollingAverage: {
                exactValue: {
                  numerator: 164,
                  denominator: 2,
                },
                approximations: [
                  { value: 82, rounding: 'PRETTY' },
                  { value: 82, rounding: 'PRECISE' },
                ],
              },
            },
          ],
        },
      ],
    };

    const { graphDialogByDayNumber, ...view } = AnalysisWeightMapper.resultDataToView(resultData);

    expect(view).toEqual({
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
              date: '2026-09-20',
              dayNumber: 1,
              includedMeasurementCount: 1,
              averageWeightInKgApproximation: 82.1,
              completeCalendarWindow: false,
            },
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
        4: {
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
        },
      },
      graph: {
        accessibleDescription:
          'Line graph of 2 measured weights and 1 rolling average from analysis day 1 to analysis day 4.',
        series: [
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
            points: [
              { x: 1, y: 82.1, intensity: 1 / 7 },
              { x: 4, y: 82, intensity: 2 / 7 },
            ],
          },
        ],
      },
    });
    expect(Object.keys(graphDialogByDayNumber)).toEqual(['1', '4']);

    expect(graphDialogByDayNumber[4]).toEqual({
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
          formattedValue: '82 kg',
          description: 'Average measured weight over the trailing 7 calendar days.',
          coverage: {
            includedValueCount: 2,
            windowInDays: 7,
          },
          calculation: {
            exactValue: {
              numerator: 164,
              denominator: 2,
            },
            prettyApproximation: '82 kg',
            preciseApproximation: '82 kg',
          },
          trend: {
            graph: {
              accessibleDescription:
                'Compact line graph of measured weight and the 7-day rolling average from 2026-09-20 to 2026-09-23.',
              series: [
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
                  points: [
                    { x: 1, y: 82.1, intensity: 1 / 7 },
                    { x: 4, y: 82, intensity: 2 / 7 },
                  ],
                },
              ],
            },
            tooltipByX: {
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
              4: {
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
              },
            },
          },
        },
      ],
    });
  });
  it('builds details for a date without a direct measurement and includes every window', () => {
    const resultData: WeightAnalysisResultData = {
      timelineStartDate: '2026-09-20',
      range: {
        startDate: '2026-09-20',
        endDate: '2026-09-21',
      },
      weightMeasurements: [
        {
          date: '2026-09-20',
          dayNumber: 1,
          weightInKg: 82.1,
        },
      ],
      rollingAverageSeries: [7, 14].map((windowSize) => ({
        windowSize,
        rollingAverages: [
          {
            date: '2026-09-21',
            dayNumber: 2,
            includedValues: [
              {
                date: '2026-09-20',
                dayNumber: 1,
                weightInKg: 82.1,
              },
            ],
            rollingAverage: {
              exactValue: {
                numerator: 82.1,
                denominator: 1,
              },
              approximations: [
                {
                  value: 82.1,
                  rounding: 'PRETTY' as const,
                },
                {
                  value: 82.1,
                  rounding: 'PRECISE' as const,
                },
              ],
            },
          },
        ],
      })),
    };

    const dialog = AnalysisWeightMapper.resultDataToView(resultData).graphDialogByDayNumber[2];

    expect(dialog?.heading).toBe('2026-09-21');
    expect(dialog?.primaryValue).toEqual({
      seriesLabel: 'Weight',
      color: '--color-action',
      formattedValue: 'No measurement',
    });

    expect(
      dialog?.rollingAverages.map((rollingAverage) => ({
        seriesLabel: rollingAverage.seriesLabel,
        formattedValue: rollingAverage.formattedValue,
        coverage: rollingAverage.coverage,
      })),
    ).toEqual([
      {
        seriesLabel: '7-day rolling average',
        formattedValue: '82.1 kg',
        coverage: {
          includedValueCount: 1,
          windowInDays: 7,
        },
      },
      {
        seriesLabel: '14-day rolling average',
        formattedValue: '82.1 kg',
        coverage: {
          includedValueCount: 1,
          windowInDays: 14,
        },
      },
    ]);
  });
});
