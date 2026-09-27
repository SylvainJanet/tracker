import { type CalendarDate, calendarDate, DateRange } from '../../../shared/api/shared.calendar';

export interface WeightAnalysisMeasurementCreation {
  readonly date: string;
  readonly dayNumber: number;
  readonly weightInKg: number;
}

export type WeightAnalysisRounding = 'PRETTY' | 'PRECISE';

export interface WeightAnalysisFractionCreation {
  readonly numerator: number;
  readonly denominator: number;
}

export interface WeightAnalysisApproximationCreation {
  readonly value: number;
  readonly rounding: WeightAnalysisRounding;
}

export interface WeightAnalysisRollingAverageValueCreation {
  readonly exactValue: WeightAnalysisFractionCreation;
  readonly approximations: readonly WeightAnalysisApproximationCreation[];
}

export interface WeightAnalysisRollingAveragePointCreation {
  readonly date: string;
  readonly dayNumber: number;
  readonly includedValues: readonly WeightAnalysisMeasurementCreation[];
  readonly rollingAverage: WeightAnalysisRollingAverageValueCreation;
}

export interface WeightAnalysisRollingAverageCreation {
  readonly windowInDays: number;
  readonly points: readonly WeightAnalysisRollingAveragePointCreation[];
}

export interface WeightAnalysisCreation {
  readonly timelineStartDate: string;
  readonly range: {
    readonly startDate: string;
    readonly endDate: string;
  };
  readonly weightMeasurements: readonly WeightAnalysisMeasurementCreation[];
  readonly rollingAverages: readonly WeightAnalysisRollingAverageCreation[];
}

export interface WeightAnalysisFraction {
  readonly numerator: number;
  readonly denominator: number;
}

export interface WeightAnalysisApproximation {
  readonly value: number;
  readonly rounding: WeightAnalysisRounding;
}

export interface WeightAnalysisRollingAverageValue {
  readonly exactValue: WeightAnalysisFraction;
  readonly approximations: readonly WeightAnalysisApproximation[];
}

export class WeightAnalysisMeasurement {
  private constructor(
    readonly date: CalendarDate,
    readonly dayNumber: number,
    private readonly weightInKg: number,
  ) {}

  static create(
    date: CalendarDate,
    dayNumber: number,
    weightInKg: number,
  ): WeightAnalysisMeasurement {
    if (!Number.isSafeInteger(dayNumber) || dayNumber < 1) {
      throw new RangeError('day number must be a positive safe integer');
    }

    if (!Number.isFinite(weightInKg) || weightInKg <= 0) {
      throw new RangeError('weight must be a positive finite number');
    }

    return new WeightAnalysisMeasurement(date, dayNumber, weightInKg);
  }

  weightInKilograms(): number {
    return this.weightInKg;
  }
}

export class WeightAnalysisRollingAveragePoint {
  private constructor(
    readonly date: CalendarDate,
    readonly dayNumber: number,
    readonly includedValues: readonly WeightAnalysisMeasurement[],
    readonly rollingAverage: WeightAnalysisRollingAverageValue,
  ) {}

  static create(
    creation: WeightAnalysisRollingAveragePointCreation,
    windowInDays: number,
  ): WeightAnalysisRollingAveragePoint {
    if (!Number.isSafeInteger(creation.dayNumber) || creation.dayNumber < 1) {
      throw new RangeError('rolling day number must be a positive safe integer');
    }

    const date = calendarDate(creation.date);
    const includedValues = creation.includedValues.map(createMeasurement);

    validateIncludedValues(includedValues, creation.dayNumber, windowInDays);

    return new WeightAnalysisRollingAveragePoint(
      date,
      creation.dayNumber,
      includedValues,
      createRollingAverageValue(creation.rollingAverage),
    );
  }
}

export class WeightAnalysisRollingAverage {
  private constructor(
    readonly windowInDays: number,
    readonly points: readonly WeightAnalysisRollingAveragePoint[],
  ) {}

  static create(creation: WeightAnalysisRollingAverageCreation): WeightAnalysisRollingAverage {
    if (!Number.isSafeInteger(creation.windowInDays) || creation.windowInDays < 1) {
      throw new RangeError('rolling window must be a positive safe integer');
    }

    return new WeightAnalysisRollingAverage(
      creation.windowInDays,
      creation.points.map((point) =>
        WeightAnalysisRollingAveragePoint.create(point, creation.windowInDays),
      ),
    );
  }
}

export class WeightAnalysis {
  private constructor(
    readonly timelineStartDate: CalendarDate,
    readonly range: DateRange,
    readonly weightMeasurements: readonly WeightAnalysisMeasurement[],
    readonly rollingAverages: readonly WeightAnalysisRollingAverage[],
  ) {}

  static create(creation: WeightAnalysisCreation): WeightAnalysis {
    const timelineStartDate = calendarDate(creation.timelineStartDate);
    const range = new DateRange(
      calendarDate(creation.range.startDate),
      calendarDate(creation.range.endDate),
    );

    if (timelineStartDate > range.startDate) {
      throw new RangeError('timeline start date must not be after represented range start date');
    }

    const measurements = creation.weightMeasurements.map(createMeasurement);
    const rollingAverages = creation.rollingAverages.map(WeightAnalysisRollingAverage.create);

    validateMeasurements(measurements, range, timelineStartDate);
    validateRollingAverages(rollingAverages, range, timelineStartDate);

    return new WeightAnalysis(timelineStartDate, range, measurements, rollingAverages);
  }
}

function createMeasurement(creation: WeightAnalysisMeasurementCreation): WeightAnalysisMeasurement {
  return WeightAnalysisMeasurement.create(
    calendarDate(creation.date),
    creation.dayNumber,
    creation.weightInKg,
  );
}

function createRollingAverageValue(
  creation: WeightAnalysisRollingAverageValueCreation,
): WeightAnalysisRollingAverageValue {
  if (
    !Number.isFinite(creation.exactValue.numerator) ||
    creation.exactValue.numerator <= 0 ||
    !Number.isFinite(creation.exactValue.denominator) ||
    creation.exactValue.denominator <= 0
  ) {
    throw new RangeError('rolling average exact fraction must contain positive finite values');
  }

  const expectedRoundings: readonly WeightAnalysisRounding[] = ['PRETTY', 'PRECISE'];
  if (
    creation.approximations.length !== expectedRoundings.length ||
    creation.approximations.some(
      (approximation, index) => approximation.rounding !== expectedRoundings[index],
    )
  ) {
    throw new RangeError('rolling average approximations must contain PRETTY then PRECISE');
  }

  if (
    creation.approximations.some(
      (approximation) => !Number.isFinite(approximation.value) || approximation.value <= 0,
    )
  ) {
    throw new RangeError('rolling average approximations must contain positive finite values');
  }

  return {
    exactValue: {
      numerator: creation.exactValue.numerator,
      denominator: creation.exactValue.denominator,
    },
    approximations: creation.approximations.map((approximation) => ({
      value: approximation.value,
      rounding: approximation.rounding,
    })),
  };
}

function validateIncludedValues(
  includedValues: readonly WeightAnalysisMeasurement[],
  pointDayNumber: number,
  windowInDays: number,
): void {
  if (includedValues.length === 0) {
    throw new RangeError('rolling average must include at least one measurement');
  }

  const firstWindowDay = Math.max(1, pointDayNumber - windowInDays + 1);
  let previousDate: CalendarDate | undefined;

  for (const includedValue of includedValues) {
    if (includedValue.dayNumber < firstWindowDay || includedValue.dayNumber > pointDayNumber) {
      throw new RangeError('included measurement must be inside the rolling window');
    }

    if (previousDate !== undefined && includedValue.date <= previousDate) {
      throw new RangeError('included measurements must be ordered by unique ascending dates');
    }

    previousDate = includedValue.date;
  }
}

function validateRollingAverages(
  rollingAverages: readonly WeightAnalysisRollingAverage[],
  range: DateRange,
  timelineStartDate: CalendarDate,
): void {
  let previousWindow: number | undefined;

  for (const rollingAverage of rollingAverages) {
    if (previousWindow !== undefined && rollingAverage.windowInDays <= previousWindow) {
      throw new RangeError('rolling averages must be ordered by unique ascending windows');
    }

    previousWindow = rollingAverage.windowInDays;
    let previousPointDate: CalendarDate | undefined;

    for (const point of rollingAverage.points) {
      if (!range.contains(point.date)) {
        throw new RangeError('rolling average point must be inside the represented range');
      }
      if (point.dayNumber !== dayNumberFor(timelineStartDate, point.date)) {
        throw new RangeError('rolling day number must match the point date on the timeline');
      }
      if (previousPointDate !== undefined && point.date <= previousPointDate) {
        throw new RangeError('rolling average points must be ordered by unique ascending dates');
      }

      for (const includedValue of point.includedValues) {
        if (includedValue.dayNumber !== dayNumberFor(timelineStartDate, includedValue.date)) {
          throw new RangeError(
            'included measurement day number must match its date on the timeline',
          );
        }
      }

      previousPointDate = point.date;
    }
  }
}

function validateMeasurements(
  measurements: readonly WeightAnalysisMeasurement[],
  range: DateRange,
  timelineStartDate: CalendarDate,
): void {
  let previousDate: CalendarDate | undefined;

  for (const measurement of measurements) {
    if (!range.contains(measurement.date)) {
      throw new RangeError('weight measurement must be inside the represented range');
    }

    if (measurement.dayNumber !== dayNumberFor(timelineStartDate, measurement.date)) {
      throw new RangeError('day number must match the measurement date on the timeline');
    }

    if (previousDate !== undefined && measurement.date <= previousDate) {
      throw new RangeError('weight measurements must be ordered by unique ascending dates');
    }

    previousDate = measurement.date;
  }
}

function dayNumberFor(timelineStartDate: CalendarDate, date: CalendarDate): number {
  const millisecondsPerDay = 24 * 60 * 60 * 1000;

  return (Date.parse(date) - Date.parse(timelineStartDate)) / millisecondsPerDay + 1;
}
