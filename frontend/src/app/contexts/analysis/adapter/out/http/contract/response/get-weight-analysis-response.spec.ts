import { describe, expect, it } from 'vitest';

import { isGetWeightAnalysisResponse } from './get-weight-analysis-response';

describe('isGetWeightAnalysisResponse', () => {
  it('accepts a populated response', () => {
    expect(isGetWeightAnalysisResponse(populatedResponse())).toBe(true);
  });

  it('accepts a globally empty response', () => {
    expect(
      isGetWeightAnalysisResponse({
        timelineStartDate: null,
        range: null,
        weightMeasurements: [],
        rollingAverageSeries: [],
      }),
    ).toBe(true);
  });

  it('limits validation to transport structure', () => {
    expect(
      isGetWeightAnalysisResponse(
        populatedResponse({
          timelineStartDate: 'not-a-calendar-date',
          range: {
            startDate: 'also-not-a-calendar-date',
            endDate: '',
          },
          weightMeasurements: [
            weightMeasurement({
              date: 'invalid-domain-date',
              dayNumber: -1.5,
              weightInKg: -82.123,
            }),
          ],
          rollingAverageSeries: [
            rollingAverageSeries({
              windowSize: -7.5,
              rollingAverages: [
                rollingAveragePoint({
                  date: 'invalid-domain-date',
                  dayNumber: -4.5,
                  includedValues: [
                    weightMeasurement({
                      date: 'invalid-domain-date',
                      dayNumber: -1.5,
                      weightInKg: -82.123,
                    }),
                  ],
                  rollingAverage: rollingAverageValue({
                    exactValue: {
                      numerator: -82.123,
                      denominator: -1.5,
                    },
                    approximations: [
                      {
                        value: -82.123,
                        rounding: 'PRETTY',
                      },
                    ],
                  }),
                }),
              ],
            }),
          ],
        }),
      ),
    ).toBe(true);
  });

  it.each([
    ['null', null],
    ['an array', []],
    ['a primitive', 'invalid'],
    ['an incomplete record', {}],
  ])('rejects %s instead of a response record', (_description, response) => {
    expect(isGetWeightAnalysisResponse(response)).toBe(false);
  });

  it.each([
    [
      'a null timeline with a populated range',
      {
        timelineStartDate: null,
        range: {
          startDate: '2026-09-20',
          endDate: '2026-09-25',
        },
        weightMeasurements: [],
        rollingAverageSeries: [],
      },
    ],
    [
      'a populated timeline with a null range',
      {
        timelineStartDate: '2026-09-20',
        range: null,
        weightMeasurements: [],
        rollingAverageSeries: [],
      },
    ],
    [
      'measurements in a globally empty response',
      {
        timelineStartDate: null,
        range: null,
        weightMeasurements: [weightMeasurement()],
        rollingAverageSeries: [],
      },
    ],
    [
      'rolling averages in a globally empty response',
      {
        timelineStartDate: null,
        range: null,
        weightMeasurements: [],
        rollingAverageSeries: [rollingAverageSeries()],
      },
    ],
  ])('rejects %s', (_description, response) => {
    expect(isGetWeightAnalysisResponse(response)).toBe(false);
  });

  it.each([
    [
      'a non-string timeline start',
      populatedResponse({
        timelineStartDate: 42,
      }),
    ],
    [
      'a non-record range',
      populatedResponse({
        range: [],
      }),
    ],
    [
      'a non-string range start',
      populatedResponse({
        range: {
          startDate: 42,
          endDate: '2026-09-25',
        },
      }),
    ],
    [
      'a non-string range end',
      populatedResponse({
        range: {
          startDate: '2026-09-20',
          endDate: 42,
        },
      }),
    ],
    [
      'a non-array measurement collection',
      populatedResponse({
        weightMeasurements: {},
      }),
    ],
    [
      'a measurement with a non-string date',
      populatedResponse({
        weightMeasurements: [weightMeasurement({ date: 42 })],
      }),
    ],
    [
      'a measurement with a non-numeric day number',
      populatedResponse({
        weightMeasurements: [weightMeasurement({ dayNumber: '1' })],
      }),
    ],
    [
      'a measurement with a non-numeric weight',
      populatedResponse({
        weightMeasurements: [weightMeasurement({ weightInKg: '82.1' })],
      }),
    ],
    [
      'the legacy rolling-average property',
      {
        timelineStartDate: '2026-09-20',
        range: {
          startDate: '2026-09-20',
          endDate: '2026-09-25',
        },
        weightMeasurements: [],
        rollingAverages: [],
      },
    ],
    [
      'a non-array rolling-average series collection',
      populatedResponse({
        rollingAverageSeries: {},
      }),
    ],
    [
      'a series with a non-numeric window size',
      populatedResponse({
        rollingAverageSeries: [rollingAverageSeries({ windowSize: '7' })],
      }),
    ],
    [
      'a series with a non-array rolling-average collection',
      populatedResponse({
        rollingAverageSeries: [rollingAverageSeries({ rollingAverages: {} })],
      }),
    ],
    ['a rolling-average point with a non-string date', responseWithRollingPoint({ date: 42 })],
    [
      'a rolling-average point with a non-numeric day number',
      responseWithRollingPoint({ dayNumber: '4' }),
    ],
    [
      'a rolling-average point with a non-array included-value collection',
      responseWithRollingPoint({ includedValues: {} }),
    ],
    [
      'a rolling-average point with an invalid included value',
      responseWithRollingPoint({
        includedValues: [weightMeasurement({ weightInKg: '82.1' })],
      }),
    ],
    [
      'a rolling-average point with a non-record value',
      responseWithRollingPoint({ rollingAverage: [] }),
    ],
    [
      'a rolling-average value with a non-record exact value',
      responseWithRollingValue({ exactValue: [] }),
    ],
    [
      'an exact value with a non-numeric numerator',
      responseWithRollingValue({
        exactValue: {
          numerator: '164.00',
          denominator: 2,
        },
      }),
    ],
    [
      'an exact value with a non-numeric denominator',
      responseWithRollingValue({
        exactValue: {
          numerator: 164,
          denominator: '2',
        },
      }),
    ],
    [
      'a rolling-average value with a non-array approximation collection',
      responseWithRollingValue({ approximations: {} }),
    ],
    [
      'an approximation with a non-numeric value',
      responseWithRollingValue({
        approximations: [
          {
            value: '82.00',
            rounding: 'PRETTY',
          },
        ],
      }),
    ],
    [
      'an approximation with an unknown rounding',
      responseWithRollingValue({
        approximations: [
          {
            value: 82,
            rounding: 'UNKNOWN',
          },
        ],
      }),
    ],
  ])('rejects %s', (_description, response) => {
    expect(isGetWeightAnalysisResponse(response)).toBe(false);
  });
});

function populatedResponse(overrides: Record<string, unknown> = {}) {
  return {
    timelineStartDate: '2026-09-20',
    range: {
      startDate: '2026-09-20',
      endDate: '2026-09-25',
    },
    weightMeasurements: [weightMeasurement()],
    rollingAverageSeries: [rollingAverageSeries()],
    ...overrides,
  };
}

function weightMeasurement(overrides: Record<string, unknown> = {}) {
  return {
    date: '2026-09-20',
    dayNumber: 1,
    weightInKg: 82.1,
    ...overrides,
  };
}

function rollingAverageSeries(overrides: Record<string, unknown> = {}) {
  return {
    windowSize: 7,
    rollingAverages: [rollingAveragePoint()],
    ...overrides,
  };
}

function rollingAveragePoint(overrides: Record<string, unknown> = {}) {
  return {
    date: '2026-09-23',
    dayNumber: 4,
    includedValues: [weightMeasurement(), weightMeasurement({ date: '2026-09-23', dayNumber: 4 })],
    rollingAverage: rollingAverageValue(),
    ...overrides,
  };
}

function rollingAverageValue(overrides: Record<string, unknown> = {}) {
  return {
    exactValue: {
      numerator: 164,
      denominator: 2,
    },
    approximations: [
      {
        value: 82,
        rounding: 'PRETTY',
      },
      {
        value: 82,
        rounding: 'PRECISE',
      },
    ],
    ...overrides,
  };
}

function responseWithRollingPoint(overrides: Record<string, unknown>) {
  return populatedResponse({
    rollingAverageSeries: [
      rollingAverageSeries({
        rollingAverages: [rollingAveragePoint(overrides)],
      }),
    ],
  });
}

function responseWithRollingValue(overrides: Record<string, unknown>) {
  return responseWithRollingPoint({
    rollingAverage: rollingAverageValue(overrides),
  });
}
