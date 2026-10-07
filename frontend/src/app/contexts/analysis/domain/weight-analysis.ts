import { type CalendarDate, type DateRange } from '../../../shared/api/shared.calendar';

export type Rounding = 'PRETTY' | 'PRECISE';

export interface Fraction {
  readonly numerator: number;
  readonly denominator: number;
}

export interface Approximation {
  readonly value: number;
  readonly rounding: Rounding;
}

export interface RollingAverageValue {
  readonly exactValue: Fraction;
  readonly approximations: readonly Approximation[];
}

export class Measurement {
  private constructor(
    readonly date: CalendarDate,
    readonly dayNumber: number,
    readonly value: number,
  ) {}

  static create(date: CalendarDate, dayNumber: number, value: number): Measurement {
    if (!Number.isSafeInteger(dayNumber) || dayNumber < 1) {
      throw new RangeError('day number must be a positive safe integer');
    }

    if (!Number.isFinite(value) || value <= 0) {
      throw new RangeError('value must be a positive finite number');
    }

    return new Measurement(date, dayNumber, value);
  }
}

export class RollingAveragePoint {
  private constructor(
    readonly date: CalendarDate,
    readonly dayNumber: number,
    readonly includedValues: readonly Measurement[],
    readonly rollingAverage: RollingAverageValue,
  ) {}

  static create(
    date: CalendarDate,
    dayNumber: number,
    includedValues: readonly Measurement[],
    rollingAverage: RollingAverageValue,
  ): RollingAveragePoint {
    if (!Number.isSafeInteger(dayNumber) || dayNumber < 1) {
      throw new RangeError('rolling day number must be a positive safe integer');
    }

    return new RollingAveragePoint(date, dayNumber, includedValues, rollingAverage);
  }
}

export class RollingAverage {
  private constructor(
    readonly windowSize: number,
    readonly rollingAverages: readonly RollingAveragePoint[],
  ) {}

  static create(
    windowSize: number,
    rollingAverages: readonly RollingAveragePoint[],
  ): RollingAverage {
    if (!Number.isSafeInteger(windowSize) || windowSize < 1) {
      throw new RangeError('rolling window must be a positive safe integer');
    }

    return new RollingAverage(windowSize, rollingAverages);
  }
}

export class Analysis {
  private constructor(
    readonly timelineStartDate: CalendarDate,
    readonly range: DateRange,
    readonly measurements: readonly Measurement[],
    readonly rollingAverageSeries: readonly RollingAverage[],
  ) {}

  static create(
    timelineStartDate: CalendarDate,
    range: DateRange,
    measurements: readonly Measurement[],
    rollingAverageSeries: readonly RollingAverage[],
  ): Analysis {
    if (timelineStartDate > range.startDate) {
      throw new RangeError('timeline start date must not be after represented range start date');
    }

    validateMeasurements(measurements, range, timelineStartDate);
    validateRollingAverageSeries(rollingAverageSeries, range, timelineStartDate);

    return new Analysis(timelineStartDate, range, measurements, rollingAverageSeries);
  }
}

function validateMeasurements(
  measurements: readonly Measurement[],
  range: DateRange,
  timelineStartDate: CalendarDate,
): void {
  let previousDate: CalendarDate | undefined;

  for (const measurement of measurements) {
    if (!range.contains(measurement.date)) {
      throw new RangeError('measurement must be inside represented range');
    }

    if (measurement.dayNumber !== dayNumberFor(timelineStartDate, measurement.date)) {
      throw new RangeError('measurement day number must match its date on timeline');
    }

    if (previousDate !== undefined && measurement.date <= previousDate) {
      throw new RangeError('measurements must be ordered by unique ascending dates');
    }

    previousDate = measurement.date;
  }
}

function validateRollingAverageSeries(
  rollingAverageSeries: readonly RollingAverage[],
  range: DateRange,
  timelineStartDate: CalendarDate,
): void {
  let previousWindowSize: number | undefined;

  for (const rollingAverage of rollingAverageSeries) {
    if (previousWindowSize !== undefined && rollingAverage.windowSize <= previousWindowSize) {
      throw new RangeError('rolling-average series must be ordered by unique ascending windows');
    }

    let previousDate: CalendarDate | undefined;

    for (const point of rollingAverage.rollingAverages) {
      if (!range.contains(point.date)) {
        throw new RangeError('rolling point must be inside represented range');
      }

      if (point.dayNumber !== dayNumberFor(timelineStartDate, point.date)) {
        throw new RangeError('rolling point day number must match its date on timeline');
      }

      if (previousDate !== undefined && point.date <= previousDate) {
        throw new RangeError('rolling points must be ordered by unique ascending dates');
      }

      if (point.includedValues.length === 0) {
        throw new RangeError('rolling point must include at least one measurement');
      }

      validateIncludedMeasurements(
        point.includedValues,
        rollingAverage.windowSize,
        point.dayNumber,
        timelineStartDate,
      );
      validateRollingAverageValue(point.rollingAverage);

      previousDate = point.date;
    }

    previousWindowSize = rollingAverage.windowSize;
  }
}

function dayNumberFor(timelineStartDate: CalendarDate, date: CalendarDate): number {
  const millisecondsPerDay = 24 * 60 * 60 * 1000;

  return (Date.parse(date) - Date.parse(timelineStartDate)) / millisecondsPerDay + 1;
}

function validateRollingAverageValue(rollingAverage: RollingAverageValue): void {
  const { numerator, denominator } = rollingAverage.exactValue;

  if (
    !Number.isFinite(numerator) ||
    numerator <= 0 ||
    !Number.isFinite(denominator) ||
    denominator <= 0
  ) {
    throw new RangeError('rolling average exact value must be positive and finite');
  }

  if (
    rollingAverage.approximations.length !== 2 ||
    rollingAverage.approximations[0]?.rounding !== 'PRETTY' ||
    rollingAverage.approximations[1]?.rounding !== 'PRECISE'
  ) {
    throw new RangeError('rolling average approximations must be ordered PRETTY then PRECISE');
  }

  for (const approximation of rollingAverage.approximations) {
    if (!Number.isFinite(approximation.value) || approximation.value <= 0) {
      throw new RangeError('rolling average approximation must be positive and finite');
    }
  }
}

function validateIncludedMeasurements(
  includedValues: readonly Measurement[],
  windowSize: number,
  rollingDayNumber: number,
  timelineStartDate: CalendarDate,
): void {
  const firstIncludedDayNumber = rollingDayNumber - windowSize + 1;
  let previousDate: CalendarDate | undefined;

  for (const measurement of includedValues) {
    if (measurement.dayNumber !== dayNumberFor(timelineStartDate, measurement.date)) {
      throw new RangeError('included measurement day number must match its date on timeline');
    }

    if (previousDate !== undefined && measurement.date <= previousDate) {
      throw new RangeError('included measurements must be ordered by unique ascending dates');
    }

    if (
      measurement.dayNumber < firstIncludedDayNumber ||
      measurement.dayNumber > rollingDayNumber
    ) {
      throw new RangeError('included measurement must be inside rolling window');
    }

    previousDate = measurement.date;
  }
}
