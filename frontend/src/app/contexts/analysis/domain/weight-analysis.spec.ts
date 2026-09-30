import { WeightAnalysis } from './weight-analysis';

describe('WeightAnalysis', () => {
  it('preserves included measurements, the exact fraction, and every approximation', () => {
    const analysis = WeightAnalysis.create({
      ...validData(),
      rollingAverages: [validRollingAverage()],
    });

    expect(
      analysis.rollingAverages.map((rollingAverage) => ({
        windowInDays: rollingAverage.windowInDays,
        points: rollingAverage.points.map((point) => ({
          date: point.date,
          dayNumber: point.dayNumber,
          includedValues: point.includedValues.map((includedValue) => ({
            date: includedValue.date,
            dayNumber: includedValue.dayNumber,
            weightInKg: includedValue.weightInKilograms(),
          })),
          rollingAverage: point.rollingAverage,
        })),
      })),
    ).toEqual([
      {
        windowInDays: 7,
        points: [
          {
            date: '2026-09-23',
            dayNumber: 4,
            includedValues: [
              { date: '2026-09-20', dayNumber: 1, weightInKg: 82.1 },
              { date: '2026-09-23', dayNumber: 4, weightInKg: 81.9 },
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
    ]);
  });

  it('creates a valid weight analysis', () => {
    const analysis = WeightAnalysis.create(validData());

    expect(analysis.timelineStartDate).toBe('2026-09-20');
    expect(analysis.range.startDate).toBe('2026-09-20');
    expect(analysis.range.endDate).toBe('2026-09-25');
    expect(
      analysis.weightMeasurements.map((measurement) => ({
        date: measurement.date,
        dayNumber: measurement.dayNumber,
        weightInKg: measurement.weightInKilograms(),
      })),
    ).toEqual([
      { date: '2026-09-20', dayNumber: 1, weightInKg: 82.1 },
      { date: '2026-09-23', dayNumber: 4, weightInKg: 81.9 },
    ]);
  });

  it('accepts a represented range without measurements', () => {
    const analysis = WeightAnalysis.create({
      ...validData(),
      weightMeasurements: [],
    });

    expect(analysis.weightMeasurements).toEqual([]);
  });

  it('accepts a represented range beginning after the timeline start', () => {
    const analysis = WeightAnalysis.create({
      ...validData(),
      range: {
        startDate: '2026-09-22',
        endDate: '2026-09-25',
      },
      weightMeasurements: [],
    });

    expect(analysis.timelineStartDate).toBe('2026-09-20');
    expect(analysis.range.startDate).toBe('2026-09-22');
  });

  it('rejects a timeline start after the represented range start', () => {
    expect(() =>
      WeightAnalysis.create({
        ...validData(),
        timelineStartDate: '2026-09-21',
        weightMeasurements: [],
      }),
    ).toThrow('timeline start date must not be after represented range start date');
  });

  it.each([
    {
      name: 'timeline start',
      data: {
        ...validData(),
        timelineStartDate: '2026-02-30',
      },
    },
    {
      name: 'range start',
      data: {
        ...validData(),
        range: {
          startDate: 'not-a-date',
          endDate: '2026-09-25',
        },
      },
    },
    {
      name: 'range end',
      data: {
        ...validData(),
        range: {
          startDate: '2026-09-20',
          endDate: '2026-09-31',
        },
      },
    },
    {
      name: 'measurement',
      data: {
        ...validData(),
        weightMeasurements: [{ date: 'invalid', dayNumber: 1, weightInKg: 82.1 }],
      },
    },
  ])('rejects an invalid $name date', ({ data }) => {
    expect(() => WeightAnalysis.create(data)).toThrow(RangeError);
  });

  it('rejects a reversed date range', () => {
    expect(() =>
      WeightAnalysis.create({
        ...validData(),
        range: {
          startDate: '2026-09-25',
          endDate: '2026-09-20',
        },
      }),
    ).toThrow(RangeError);
  });

  it.each([Number.NaN, Number.POSITIVE_INFINITY, Number.NEGATIVE_INFINITY, 0, -1])(
    'rejects an invalid measured weight: %s',
    (weightInKg) => {
      expect(() =>
        WeightAnalysis.create({
          ...validData(),
          weightMeasurements: [{ date: '2026-09-20', dayNumber: 1, weightInKg }],
        }),
      ).toThrow(RangeError);
    },
  );

  it('accepts a positive finite value that does not follow Journal weight increments', () => {
    const analysis = WeightAnalysis.create({
      ...validData(),
      weightMeasurements: [{ date: '2026-09-20', dayNumber: 1, weightInKg: 82.11 }],
    });

    expect(analysis.weightMeasurements[0]?.weightInKilograms()).toBe(82.11);
  });

  it.each([Number.NaN, Number.POSITIVE_INFINITY, 0, -1, 1.5])(
    'rejects an invalid day number: %s',
    (dayNumber) => {
      expect(() =>
        WeightAnalysis.create({
          ...validData(),
          weightMeasurements: [{ date: '2026-09-20', dayNumber, weightInKg: 82.1 }],
        }),
      ).toThrow(RangeError);
    },
  );

  it('rejects a measurement outside the represented range', () => {
    expect(() =>
      WeightAnalysis.create({
        ...validData(),
        weightMeasurements: [{ date: '2026-09-19', dayNumber: 1, weightInKg: 82.1 }],
      }),
    ).toThrow(RangeError);
  });

  it.each([
    ['duplicate', '2026-09-20', 1, '2026-09-20', 1],
    ['descending', '2026-09-23', 4, '2026-09-20', 1],
  ])(
    'rejects %s measurement dates',
    (_case, firstDate, firstDayNumber, secondDate, secondDayNumber) => {
      expect(() =>
        WeightAnalysis.create({
          ...validData(),
          weightMeasurements: [
            { date: firstDate, dayNumber: firstDayNumber, weightInKg: 82.1 },
            { date: secondDate, dayNumber: secondDayNumber, weightInKg: 81.9 },
          ],
        }),
      ).toThrow(RangeError);
    },
  );

  it('rejects a day number that does not match its date on the timeline', () => {
    expect(() =>
      WeightAnalysis.create({
        ...validData(),
        weightMeasurements: [{ date: '2026-09-23', dayNumber: 3, weightInKg: 82.1 }],
      }),
    ).toThrow('day number must match the measurement date on the timeline');
  });

  it.each([Number.NaN, Number.POSITIVE_INFINITY, 0, -1, 1.5])(
    'rejects an invalid rolling window: %s',
    (windowInDays) => {
      expect(() =>
        WeightAnalysis.create({
          ...validData(),
          rollingAverages: [{ windowInDays, points: [] }],
        }),
      ).toThrow('rolling window must be a positive safe integer');
    },
  );

  it.each([Number.NaN, Number.POSITIVE_INFINITY, Number.NEGATIVE_INFINITY, 0, -1])(
    'rejects an invalid exact numerator: %s',
    (numerator) => {
      const rollingAverage = validRollingAverage();
      const point = rollingAverage.points[0]!;

      expect(() =>
        WeightAnalysis.create({
          ...validData(),
          rollingAverages: [
            {
              ...rollingAverage,
              points: [
                {
                  ...point,
                  rollingAverage: {
                    ...point.rollingAverage,
                    exactValue: { ...point.rollingAverage.exactValue, numerator },
                  },
                },
              ],
            },
          ],
        }),
      ).toThrow('rolling average exact fraction must contain positive finite values');
    },
  );

  it.each([Number.NaN, Number.POSITIVE_INFINITY, Number.NEGATIVE_INFINITY, 0, -1])(
    'rejects an invalid exact denominator: %s',
    (denominator) => {
      const rollingAverage = validRollingAverage();
      const point = rollingAverage.points[0]!;

      expect(() =>
        WeightAnalysis.create({
          ...validData(),
          rollingAverages: [
            {
              ...rollingAverage,
              points: [
                {
                  ...point,
                  rollingAverage: {
                    ...point.rollingAverage,
                    exactValue: { ...point.rollingAverage.exactValue, denominator },
                  },
                },
              ],
            },
          ],
        }),
      ).toThrow('rolling average exact fraction must contain positive finite values');
    },
  );

  it.each([
    { approximations: [] },
    { approximations: [{ value: 82, rounding: 'PRETTY' as const }] },
    {
      approximations: [
        { value: 82, rounding: 'PRECISE' as const },
        { value: 82, rounding: 'PRETTY' as const },
      ],
    },
    {
      approximations: [
        { value: 82, rounding: 'PRETTY' as const },
        { value: 82, rounding: 'PRETTY' as const },
      ],
    },
  ])('rejects missing, reordered, or duplicate approximations', ({ approximations }) => {
    const rollingAverage = validRollingAverage();
    const point = rollingAverage.points[0]!;

    expect(() =>
      WeightAnalysis.create({
        ...validData(),
        rollingAverages: [
          {
            ...rollingAverage,
            points: [
              {
                ...point,
                rollingAverage: { ...point.rollingAverage, approximations },
              },
            ],
          },
        ],
      }),
    ).toThrow('rolling average approximations must contain PRETTY then PRECISE');
  });

  it.each([Number.NaN, Number.POSITIVE_INFINITY, Number.NEGATIVE_INFINITY, 0, -1])(
    'rejects an invalid approximation value: %s',
    (value) => {
      const rollingAverage = validRollingAverage();
      const point = rollingAverage.points[0]!;

      expect(() =>
        WeightAnalysis.create({
          ...validData(),
          rollingAverages: [
            {
              ...rollingAverage,
              points: [
                {
                  ...point,
                  rollingAverage: {
                    ...point.rollingAverage,
                    approximations: [
                      { value, rounding: 'PRETTY' },
                      { value: 82, rounding: 'PRECISE' },
                    ],
                  },
                },
              ],
            },
          ],
        }),
      ).toThrow('rolling average approximations must contain positive finite values');
    },
  );

  it('rejects a rolling average without included measurements', () => {
    const rollingAverage = validRollingAverage();

    expect(() =>
      WeightAnalysis.create({
        ...validData(),
        rollingAverages: [
          {
            ...rollingAverage,
            points: [{ ...rollingAverage.points[0]!, includedValues: [] }],
          },
        ],
      }),
    ).toThrow('rolling average must include at least one measurement');
  });

  it('rejects an included measurement outside the rolling window', () => {
    const rollingAverage = validRollingAverage();

    expect(() =>
      WeightAnalysis.create({
        ...validData(),
        rollingAverages: [
          {
            ...rollingAverage,
            windowInDays: 2,
          },
        ],
      }),
    ).toThrow('included measurement must be inside the rolling window');
  });

  it('rejects duplicate or descending included measurements', () => {
    const rollingAverage = validRollingAverage();
    const point = rollingAverage.points[0]!;
    const firstIncludedValue = point.includedValues[0]!;

    expect(() =>
      WeightAnalysis.create({
        ...validData(),
        rollingAverages: [
          {
            ...rollingAverage,
            points: [
              {
                ...point,
                includedValues: [firstIncludedValue, firstIncludedValue],
              },
            ],
          },
        ],
      }),
    ).toThrow('included measurements must be ordered by unique ascending dates');

    expect(() =>
      WeightAnalysis.create({
        ...validData(),
        rollingAverages: [
          {
            ...rollingAverage,
            points: [{ ...point, includedValues: [...point.includedValues].reverse() }],
          },
        ],
      }),
    ).toThrow('included measurements must be ordered by unique ascending dates');
  });

  it('rejects an included day number that does not match its date', () => {
    const rollingAverage = validRollingAverage();
    const point = rollingAverage.points[0]!;

    expect(() =>
      WeightAnalysis.create({
        ...validData(),
        rollingAverages: [
          {
            ...rollingAverage,
            points: [
              {
                ...point,
                includedValues: [
                  { ...point.includedValues[0]!, dayNumber: 2 },
                  point.includedValues[1]!,
                ],
              },
            ],
          },
        ],
      }),
    ).toThrow('included measurement day number must match its date on the timeline');
  });

  it('rejects a rolling point outside the represented range', () => {
    const rollingAverage = validRollingAverage();
    const point = rollingAverage.points[0]!;

    expect(() =>
      WeightAnalysis.create({
        ...validData(),
        rollingAverages: [
          {
            ...rollingAverage,
            points: [
              {
                ...point,
                date: '2026-09-26',
                dayNumber: 7,
              },
            ],
          },
        ],
      }),
    ).toThrow('rolling average point must be inside the represented range');
  });

  it('rejects a rolling day number that does not match its date on the timeline', () => {
    const rollingAverage = validRollingAverage();

    expect(() =>
      WeightAnalysis.create({
        ...validData(),
        rollingAverages: [
          {
            ...rollingAverage,
            points: [
              {
                ...rollingAverage.points[0]!,
                dayNumber: 3,
                includedValues: [rollingAverage.points[0]!.includedValues[0]!],
              },
            ],
          },
        ],
      }),
    ).toThrow('rolling day number must match the point date on the timeline');
  });

  it.each([
    [7, 7],
    [14, 7],
  ])('rejects duplicate or descending rolling windows: %s', (firstWindow, secondWindow) => {
    const rollingAverage = validRollingAverage();

    expect(() =>
      WeightAnalysis.create({
        ...validData(),
        rollingAverages: [
          { ...rollingAverage, windowInDays: firstWindow },
          { ...rollingAverage, windowInDays: secondWindow },
        ],
      }),
    ).toThrow('rolling averages must be ordered by unique ascending windows');
  });

  it('rejects duplicate or descending rolling point dates', () => {
    const rollingAverage = validRollingAverage();
    const point = rollingAverage.points[0]!;
    const earlierPoint = {
      ...point,
      date: '2026-09-20',
      dayNumber: 1,
      includedValues: [point.includedValues[0]!],
      rollingAverage: {
        exactValue: { numerator: 82.1, denominator: 1 },
        approximations: [
          { value: 82.1, rounding: 'PRETTY' as const },
          { value: 82.1, rounding: 'PRECISE' as const },
        ],
      },
    };

    expect(() =>
      WeightAnalysis.create({
        ...validData(),
        rollingAverages: [{ ...rollingAverage, points: [point, point] }],
      }),
    ).toThrow('rolling average points must be ordered by unique ascending dates');

    expect(() =>
      WeightAnalysis.create({
        ...validData(),
        rollingAverages: [{ ...rollingAverage, points: [point, earlierPoint] }],
      }),
    ).toThrow('rolling average points must be ordered by unique ascending dates');
  });
});

function validData() {
  return {
    timelineStartDate: '2026-09-20',
    range: {
      startDate: '2026-09-20',
      endDate: '2026-09-25',
    },
    weightMeasurements: [
      { date: '2026-09-20', dayNumber: 1, weightInKg: 82.1 },
      { date: '2026-09-23', dayNumber: 4, weightInKg: 81.9 },
    ],
    rollingAverages: [],
  };
}

function validRollingAverage() {
  return {
    windowInDays: 7,
    points: [
      {
        date: '2026-09-23',
        dayNumber: 4,
        includedValues: [
          { date: '2026-09-20', dayNumber: 1, weightInKg: 82.1 },
          { date: '2026-09-23', dayNumber: 4, weightInKg: 81.9 },
        ],
        rollingAverage: {
          exactValue: {
            numerator: 164,
            denominator: 2,
          },
          approximations: [
            { value: 82, rounding: 'PRETTY' as const },
            { value: 82, rounding: 'PRECISE' as const },
          ],
        },
      },
    ],
  };
}
