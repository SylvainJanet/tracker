import { type Weight } from './weight';
import type { CalendarDate } from '../../../shared/api/shared.calendar';

export class WeightMeasurement {
  constructor(
    readonly calendarDate: CalendarDate,
    readonly weight: Weight,
  ) {
    if (!calendarDate) {
      throw new Error('calendarDate is required');
    }
    if (!weight) {
      throw new Error('weight is required');
    }
  }

  public static create(calendarDate: CalendarDate, weight: Weight): WeightMeasurement {
    return new WeightMeasurement(calendarDate, weight);
  }

  public weightInKilograms(): number {
    return this.weight.inKilograms();
  }
}
