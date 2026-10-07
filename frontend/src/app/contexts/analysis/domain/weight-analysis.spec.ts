import { describe, expect, it } from 'vitest';

import {
  Analysis,
  Measurement,
  RollingAverage,
  RollingAveragePoint,
  type RollingAverageValue,
} from './weight-analysis';
import { calendarDate, DateRange } from '../../../shared/api/shared.calendar';

describe('Measurement', () => {
  it('creates a measurement', () => {
    const measurement = Measurement.create(calendarDate('2026-09-20'), 1, 82.11);

    expect(measurement.date).toBe('2026-09-20');
    expect(measurement.dayNumber).toBe(1);
    expect(measurement.value).toBe(82.11);
  });

  it.each([
    Number.NaN,
    Number.POSITIVE_INFINITY,
    Number.NEGATIVE_INFINITY,
    0,
    -1,
    1.5,
    Number.MAX_SAFE_INTEGER + 1,
  ])('rejects an invalid day number: %s', (dayNumber) => {
    expect(() => Measurement.create(calendarDate('2026-09-20'), dayNumber, 82.1)).toThrow(
      'day number must be a positive safe integer',
    );
  });

  it.each([Number.NaN, Number.POSITIVE_INFINITY, Number.NEGATIVE_INFINITY, 0, -1])(
    'rejects an invalid value: %s',
    (value) => {
      expect(() => Measurement.create(calendarDate('2026-09-20'), 1, value)).toThrow(
        'value must be a positive finite number',
      );
    },
  );
});

describe('RollingAveragePoint', () => {
  it('preserves its included measurements and calculated value', () => {
    const includedValues = validMeasurements();
    const value = validRollingAverageValue();

    const point = RollingAveragePoint.create(calendarDate('2026-09-23'), 4, includedValues, value);

    expect(point.date).toBe('2026-09-23');
    expect(point.dayNumber).toBe(4);
    expect(point.includedValues).toEqual(includedValues);
    expect(point.rollingAverage).toEqual(value);
  });

  it.each([
    Number.NaN,
    Number.POSITIVE_INFINITY,
    Number.NEGATIVE_INFINITY,
    0,
    -1,
    1.5,
    Number.MAX_SAFE_INTEGER + 1,
  ])('rejects an invalid rolling day number: %s', (dayNumber) => {
    expect(() =>
      RollingAveragePoint.create(
        calendarDate('2026-09-23'),
        dayNumber,
        validMeasurements(),
        validRollingAverageValue(),
      ),
    ).toThrow('rolling day number must be a positive safe integer');
  });
});

describe('RollingAverage', () => {
  it('creates a rolling-average series', () => {
    const points = [validRollingAveragePoint()];

    const rollingAverage = RollingAverage.create(7, points);

    expect(rollingAverage.windowSize).toBe(7);
    expect(rollingAverage.rollingAverages).toEqual(points);
  });

  it('accepts a series without calculated points', () => {
    expect(RollingAverage.create(7, []).rollingAverages).toEqual([]);
  });

  it.each([
    Number.NaN,
    Number.POSITIVE_INFINITY,
    Number.NEGATIVE_INFINITY,
    0,
    -1,
    1.5,
    Number.MAX_SAFE_INTEGER + 1,
  ])('rejects an invalid rolling window: %s', (windowSize) => {
    expect(() => RollingAverage.create(windowSize, [])).toThrow(
      'rolling window must be a positive safe integer',
    );
  });
});

describe('Analysis', () => {
  it('preserves the timeline, represented range, measurements, and rolling averages', () => {
    const range = DateRange.create(calendarDate('2026-09-20'), calendarDate('2026-09-25'));
    const measurements = validMeasurements();
    const rollingAverageSeries = [RollingAverage.create(7, [validRollingAveragePoint()])];

    const analysis = Analysis.create(
      calendarDate('2026-09-20'),
      range,
      measurements,
      rollingAverageSeries,
    );

    expect(analysis.timelineStartDate).toBe('2026-09-20');
    expect(analysis.range).toEqual(range);
    expect(analysis.measurements).toEqual(measurements);
    expect(analysis.rollingAverageSeries).toEqual(rollingAverageSeries);
  });

  it('accepts an analysis without measurements or rolling-average series', () => {
    const range = DateRange.create(calendarDate('2026-09-20'), calendarDate('2026-09-25'));

    const analysis = Analysis.create(calendarDate('2026-09-20'), range, [], []);

    expect(analysis.measurements).toEqual([]);
    expect(analysis.rollingAverageSeries).toEqual([]);
  });

  it('rejects a timeline start after the represented range start', () => {
    const range = DateRange.create(calendarDate('2026-09-20'), calendarDate('2026-09-25'));

    expect(() => Analysis.create(calendarDate('2026-09-21'), range, [], [])).toThrow(
      'timeline start date must not be after represented range start date',
    );
  });

  it('rejects a measurement outside the represented range', () => {
    const range = DateRange.create(calendarDate('2026-09-20'), calendarDate('2026-09-25'));
    const measurements = [Measurement.create(calendarDate('2026-09-26'), 7, 82.1)];

    expect(() => Analysis.create(calendarDate('2026-09-20'), range, measurements, [])).toThrow(
      'measurement must be inside represented range',
    );
  });

  it('rejects a measurement day number that does not match its date', () => {
    const range = DateRange.create(calendarDate('2026-09-20'), calendarDate('2026-09-25'));
    const measurements = [Measurement.create(calendarDate('2026-09-23'), 3, 82.1)];

    expect(() => Analysis.create(calendarDate('2026-09-20'), range, measurements, [])).toThrow(
      'measurement day number must match its date on timeline',
    );
  });

  it('rejects measurements that are not ordered by unique ascending dates', () => {
    const range = DateRange.create(calendarDate('2026-09-20'), calendarDate('2026-09-25'));

    expect(() =>
      Analysis.create(calendarDate('2026-09-20'), range, [...validMeasurements()].reverse(), []),
    ).toThrow('measurements must be ordered by unique ascending dates');
  });

  it('rejects a rolling point day number that does not match its date', () => {
    const range = DateRange.create(calendarDate('2026-09-20'), calendarDate('2026-09-25'));
    const point = RollingAveragePoint.create(
      calendarDate('2026-09-23'),
      3,
      validMeasurements(),
      validRollingAverageValue(),
    );

    expect(() =>
      Analysis.create(calendarDate('2026-09-20'), range, validMeasurements(), [
        RollingAverage.create(7, [point]),
      ]),
    ).toThrow('rolling point day number must match its date on timeline');
  });

  it('rejects rolling points that are not ordered by unique ascending dates', () => {
    const range = DateRange.create(calendarDate('2026-09-20'), calendarDate('2026-09-25'));
    const laterPoint = RollingAveragePoint.create(
      calendarDate('2026-09-24'),
      5,
      validMeasurements(),
      validRollingAverageValue(),
    );

    expect(() =>
      Analysis.create(calendarDate('2026-09-20'), range, validMeasurements(), [
        RollingAverage.create(7, [laterPoint, validRollingAveragePoint()]),
      ]),
    ).toThrow('rolling points must be ordered by unique ascending dates');
  });

  it('rejects a rolling point without an included measurement', () => {
    const range = DateRange.create(calendarDate('2026-09-20'), calendarDate('2026-09-25'));
    const point = RollingAveragePoint.create(
      calendarDate('2026-09-23'),
      4,
      [],
      validRollingAverageValue(),
    );

    expect(() =>
      Analysis.create(calendarDate('2026-09-20'), range, validMeasurements(), [
        RollingAverage.create(7, [point]),
      ]),
    ).toThrow('rolling point must include at least one measurement');
  });

  it('rejects an included measurement day number that does not match its date', () => {
    const range = DateRange.create(calendarDate('2026-09-20'), calendarDate('2026-09-25'));
    const includedValues = [Measurement.create(calendarDate('2026-09-23'), 3, 81.9)];
    const point = RollingAveragePoint.create(
      calendarDate('2026-09-23'),
      4,
      includedValues,
      validRollingAverageValue(),
    );

    expect(() =>
      Analysis.create(calendarDate('2026-09-20'), range, validMeasurements(), [
        RollingAverage.create(7, [point]),
      ]),
    ).toThrow('included measurement day number must match its date on timeline');
  });

  it('rejects included measurements that are not ordered by unique ascending dates', () => {
    const range = DateRange.create(calendarDate('2026-09-20'), calendarDate('2026-09-25'));
    const point = RollingAveragePoint.create(
      calendarDate('2026-09-23'),
      4,
      [...validMeasurements()].reverse(),
      validRollingAverageValue(),
    );

    expect(() =>
      Analysis.create(calendarDate('2026-09-20'), range, validMeasurements(), [
        RollingAverage.create(7, [point]),
      ]),
    ).toThrow('included measurements must be ordered by unique ascending dates');
  });

  it('rejects an included measurement outside the trailing rolling window', () => {
    const range = DateRange.create(calendarDate('2026-09-20'), calendarDate('2026-09-29'));
    const point = RollingAveragePoint.create(
      calendarDate('2026-09-29'),
      10,
      [Measurement.create(calendarDate('2026-09-20'), 1, 82.1)],
      validRollingAverageValue(),
    );

    expect(() =>
      Analysis.create(calendarDate('2026-09-20'), range, validMeasurements(), [
        RollingAverage.create(7, [point]),
      ]),
    ).toThrow('included measurement must be inside rolling window');
  });

  it.each([Number.NaN, Number.POSITIVE_INFINITY, Number.NEGATIVE_INFINITY, 0, -1])(
    'rejects an invalid rolling-average numerator: %s',
    (numerator) => {
      const rollingAverage = validRollingAverageValue();
      const value: RollingAverageValue = {
        ...rollingAverage,
        exactValue: {
          ...rollingAverage.exactValue,
          numerator,
        },
      };

      expect(() => createAnalysisWithRollingAverageValue(value)).toThrow(
        'rolling average exact value must be positive and finite',
      );
    },
  );

  it.each([Number.NaN, Number.POSITIVE_INFINITY, Number.NEGATIVE_INFINITY, 0, -1])(
    'rejects an invalid rolling-average denominator: %s',
    (denominator) => {
      const rollingAverage = validRollingAverageValue();
      const value: RollingAverageValue = {
        ...rollingAverage,
        exactValue: {
          ...rollingAverage.exactValue,
          denominator,
        },
      };

      expect(() => createAnalysisWithRollingAverageValue(value)).toThrow(
        'rolling average exact value must be positive and finite',
      );
    },
  );

  it.each([Number.NaN, Number.POSITIVE_INFINITY, Number.NEGATIVE_INFINITY, 0, -1])(
    'rejects an invalid rolling-average approximation: %s',
    (approximation) => {
      const value: RollingAverageValue = {
        ...validRollingAverageValue(),
        approximations: [
          { value: approximation, rounding: 'PRETTY' },
          { value: 82, rounding: 'PRECISE' },
        ],
      };

      expect(() => createAnalysisWithRollingAverageValue(value)).toThrow(
        'rolling average approximation must be positive and finite',
      );
    },
  );

  it('rejects missing or unordered rolling-average approximations', () => {
    const value: RollingAverageValue = {
      ...validRollingAverageValue(),
      approximations: [
        { value: 82, rounding: 'PRECISE' },
        { value: 82, rounding: 'PRETTY' },
      ],
    };

    expect(() => createAnalysisWithRollingAverageValue(value)).toThrow(
      'rolling average approximations must be ordered PRETTY then PRECISE',
    );
  });

  it('rejects rolling-average series that are not ordered by unique ascending windows', () => {
    const range = DateRange.create(calendarDate('2026-09-20'), calendarDate('2026-09-25'));

    expect(() =>
      Analysis.create(calendarDate('2026-09-20'), range, validMeasurements(), [
        RollingAverage.create(14, []),
        RollingAverage.create(7, []),
      ]),
    ).toThrow('rolling-average series must be ordered by unique ascending windows');
  });
});

function validMeasurements(): readonly Measurement[] {
  return [
    Measurement.create(calendarDate('2026-09-20'), 1, 82.1),
    Measurement.create(calendarDate('2026-09-23'), 4, 81.9),
  ];
}

function validRollingAveragePoint(): RollingAveragePoint {
  return RollingAveragePoint.create(
    calendarDate('2026-09-23'),
    4,
    validMeasurements(),
    validRollingAverageValue(),
  );
}

function validRollingAverageValue(): RollingAverageValue {
  return {
    exactValue: {
      numerator: 164,
      denominator: 2,
    },
    approximations: [
      { value: 82, rounding: 'PRETTY' },
      { value: 82, rounding: 'PRECISE' },
    ],
  };
}

function createAnalysisWithRollingAverageValue(value: RollingAverageValue): Analysis {
  const range = DateRange.create(calendarDate('2026-09-20'), calendarDate('2026-09-25'));
  const point = RollingAveragePoint.create(
    calendarDate('2026-09-23'),
    4,
    validMeasurements(),
    value,
  );

  return Analysis.create(calendarDate('2026-09-20'), range, validMeasurements(), [
    RollingAverage.create(7, [point]),
  ]);
}
