import { WeightAnalysis } from './weight-analysis';

describe('WeightAnalysis', () => {
  it('creates a valid weight analysis', () => {
    const analysis = WeightAnalysis.create(validData());

    expect(analysis.timelineStartDate).toBe('2026-09-20');
    expect(analysis.range.startDate).toBe('2026-09-20');
    expect(analysis.range.endDate).toBe('2026-09-25');
    expect(
      analysis.weightMeasurements.map((measurement) => ({
        date: measurement.calendarDate,
        dayNumber: measurement.dayNumber,
        weightInKg: measurement.weightInKilograms(),
      })),
    ).toEqual([
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
        weightMeasurements: [
          {
            date: 'invalid',
            dayNumber: 1,
            weightInKg: 82.1,
          },
        ],
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

  it.each([Number.NaN, Number.POSITIVE_INFINITY, 0, -1, 82.11])(
    'rejects an invalid measured weight: %s',
    (weightInKg) => {
      expect(() =>
        WeightAnalysis.create({
          ...validData(),
          weightMeasurements: [
            {
              date: '2026-09-20',
              dayNumber: 1,
              weightInKg,
            },
          ],
        }),
      ).toThrow(RangeError);
    },
  );

  it.each([Number.NaN, Number.POSITIVE_INFINITY, 0, -1, 1.5])(
    'rejects an invalid day number: %s',
    (dayNumber) => {
      expect(() =>
        WeightAnalysis.create({
          ...validData(),
          weightMeasurements: [
            {
              date: '2026-09-20',
              dayNumber,
              weightInKg: 82.1,
            },
          ],
        }),
      ).toThrow(RangeError);
    },
  );

  it('rejects a measurement outside the represented range', () => {
    expect(() =>
      WeightAnalysis.create({
        ...validData(),
        weightMeasurements: [
          {
            date: '2026-09-19',
            dayNumber: 1,
            weightInKg: 82.1,
          },
        ],
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
            {
              date: firstDate,
              dayNumber: firstDayNumber,
              weightInKg: 82.1,
            },
            {
              date: secondDate,
              dayNumber: secondDayNumber,
              weightInKg: 81.9,
            },
          ],
        }),
      ).toThrow(RangeError);
    },
  );
});

function validData() {
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
      {
        date: '2026-09-23',
        dayNumber: 4,
        weightInKg: 81.9,
      },
    ],
  };
}
