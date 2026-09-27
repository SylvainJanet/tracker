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
      rollingAverages: [
        {
          windowInDays: 7,
          points: [
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

    expect(AnalysisWeightMapper.resultDataToView(resultData)).toEqual({
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
  });
});
