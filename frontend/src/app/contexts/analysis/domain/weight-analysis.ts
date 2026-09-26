import { type CalendarDate, calendarDate, DateRange } from '../../../shared/api/shared.calendar';

export interface WeightAnalysisCreation {
  readonly timelineStartDate: string;
  readonly range: {
    readonly startDate: string;
    readonly endDate: string;
  };
  readonly weightMeasurements: readonly {
    readonly date: string;
    readonly dayNumber: number;
    readonly weightInKg: number;
  }[];
}

export class WeightAnalysisMeasurement {
  private constructor(
    readonly calendarDate: CalendarDate,
    readonly dayNumber: number,
    private readonly weightInKg: number,
  ) {}

  static create(
    calendarDate: CalendarDate,
    dayNumber: number,
    weightInKg: number,
  ): WeightAnalysisMeasurement {
    if (!Number.isSafeInteger(dayNumber) || dayNumber < 1) {
      throw new RangeError('day number must be a positive safe integer');
    }

    if (!Number.isFinite(weightInKg) || weightInKg <= 0) {
      throw new RangeError('weight must be a positive finite number');
    }

    return new WeightAnalysisMeasurement(calendarDate, dayNumber, weightInKg);
  }

  weightInKilograms(): number {
    return this.weightInKg;
  }
}

export class WeightAnalysis {
  private constructor(
    readonly timelineStartDate: CalendarDate,
    readonly range: DateRange,
    readonly weightMeasurements: readonly WeightAnalysisMeasurement[],
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
    const measurements = creation.weightMeasurements.map((measurement) =>
      WeightAnalysisMeasurement.create(
        calendarDate(measurement.date),
        measurement.dayNumber,
        measurement.weightInKg,
      ),
    );

    validateMeasurements(measurements, range, timelineStartDate);

    return new WeightAnalysis(timelineStartDate, range, [...measurements]);
  }
}

function validateMeasurements(
  measurements: readonly WeightAnalysisMeasurement[],
  range: DateRange,
  timelineStartDate: CalendarDate,
): void {
  let previousDate: CalendarDate | undefined;

  for (const measurement of measurements) {
    if (!range.contains(measurement.calendarDate)) {
      throw new RangeError('weight measurement must be inside the represented range');
    }

    if (measurement.dayNumber !== dayNumberFor(timelineStartDate, measurement.calendarDate)) {
      throw new RangeError('day number must match the measurement date on the timeline');
    }

    if (previousDate !== undefined && measurement.calendarDate <= previousDate) {
      throw new RangeError('weight measurements must be ordered by unique ascending dates');
    }

    previousDate = measurement.calendarDate;
  }
}

function dayNumberFor(timelineStartDate: CalendarDate, date: CalendarDate): number {
  const millisecondsPerDay = 24 * 60 * 60 * 1000;

  return (Date.parse(date) - Date.parse(timelineStartDate)) / millisecondsPerDay + 1;
}
