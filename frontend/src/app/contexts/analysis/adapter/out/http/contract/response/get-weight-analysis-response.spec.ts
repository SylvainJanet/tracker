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
        rollingAverages: [],
      }),
    ).toBe(true);
  });

  it('limits validation to transport structure', () => {
    expect(
      isGetWeightAnalysisResponse({
        timelineStartDate: 'not-a-calendar-date',
        range: {
          startDate: 'also-not-a-calendar-date',
          endDate: '',
        },
        weightMeasurements: [
          {
            date: 'invalid-domain-date',
            dayNumber: -1.5,
            weightInKg: -82.123,
          },
        ],
        rollingAverages: [],
      }),
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
        rollingAverages: [],
      },
    ],
    [
      'a populated timeline with a null range',
      {
        timelineStartDate: '2026-09-20',
        range: null,
        weightMeasurements: [],
        rollingAverages: [],
      },
    ],
    [
      'measurements in a globally empty response',
      {
        timelineStartDate: null,
        range: null,
        weightMeasurements: [
          {
            date: '2026-09-20',
            dayNumber: 1,
            weightInKg: 82.1,
          },
        ],
        rollingAverages: [],
      },
    ],
  ])('rejects %s', (_description, response) => {
    expect(isGetWeightAnalysisResponse(response)).toBe(false);
  });

  it.each([
    [
      'a non-string timeline start',
      {
        ...populatedResponse(),
        timelineStartDate: 42,
      },
    ],
    [
      'a non-record range',
      {
        ...populatedResponse(),
        range: [],
      },
    ],
    [
      'a non-string range start',
      {
        ...populatedResponse(),
        range: {
          startDate: 42,
          endDate: '2026-09-25',
        },
      },
    ],
    [
      'a non-string range end',
      {
        ...populatedResponse(),
        range: {
          startDate: '2026-09-20',
          endDate: 42,
        },
      },
    ],
    [
      'a non-array measurement collection',
      {
        ...populatedResponse(),
        weightMeasurements: {},
      },
    ],
    [
      'a measurement with a non-string date',
      {
        ...populatedResponse(),
        weightMeasurements: [
          {
            date: 42,
            dayNumber: 1,
            weightInKg: 82.1,
          },
        ],
      },
    ],
    [
      'a measurement with a non-numeric day number',
      {
        ...populatedResponse(),
        weightMeasurements: [
          {
            date: '2026-09-20',
            dayNumber: '1',
            weightInKg: 82.1,
          },
        ],
      },
    ],
    [
      'a measurement with a non-numeric weight',
      {
        ...populatedResponse(),
        weightMeasurements: [
          {
            date: '2026-09-20',
            dayNumber: 1,
            weightInKg: '82.1',
          },
        ],
      },
    ],
  ])('rejects %s', (_description, response) => {
    expect(isGetWeightAnalysisResponse(response)).toBe(false);
  });

  it('rejects rolling averages until that response is supported', () => {
    expect(
      isGetWeightAnalysisResponse({
        ...populatedResponse(),
        rollingAverages: [
          {
            windowInDays: 7,
            points: [],
          },
        ],
      }),
    ).toBe(false);
  });
});

function populatedResponse() {
  return {
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
    ],
    rollingAverages: [],
  };
}
