import { type CalendarDate, calendarDate, DateRange } from '../../../shared/api/shared.calendar';
import { Weight, WeightMeasurement } from '../../../shared/api/shared.weight-measurement';

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
    private readonly measurement: WeightMeasurement,
    readonly dayNumber: number,
  ) {}

  static create(measurement: WeightMeasurement, dayNumber: number): WeightAnalysisMeasurement {
    if (!Number.isSafeInteger(dayNumber) || dayNumber < 1) {
      throw new RangeError('day number must be a positive safe integer');
    }

    return new WeightAnalysisMeasurement(measurement, dayNumber);
  }

  get calendarDate(): CalendarDate {
    return this.measurement.calendarDate;
  }

  weightInKilograms(): number {
    return this.measurement.weightInKilograms();
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
        WeightMeasurement.create(calendarDate(measurement.date), Weight.of(measurement.weightInKg)),
        measurement.dayNumber,
      ),
    );

    validateMeasurements(measurements, range);

    return new WeightAnalysis(timelineStartDate, range, [...measurements]);
  }
}

function validateMeasurements(
  measurements: readonly WeightAnalysisMeasurement[],
  range: DateRange,
): void {
  let previousDate: CalendarDate | undefined;

  for (const measurement of measurements) {
    if (!range.contains(measurement.calendarDate)) {
      throw new RangeError('weight measurement must be inside the represented range');
    }

    if (previousDate !== undefined && measurement.calendarDate <= previousDate) {
      throw new RangeError('weight measurements must be ordered by unique ascending dates');
    }

    previousDate = measurement.calendarDate;
  }
}
