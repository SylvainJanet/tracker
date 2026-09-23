import type {
  GetWeightMeasurementByDateCriteria,
  GetWeightMeasurementByDateOutcomeData,
  WeightMeasurementStore,
} from '../port/out/weight-measurement.store';
import { type CalendarDate, calendarDate } from '../../domain/calendar-date';
import type {
  GetWeightMeasurementByDateQuery,
  GetWeightMeasurementByDateResult,
  GetWeightMeasurementByDateResultData,
  GetWeightMeasurementByDateUseCase,
} from '../port/in/get-weight-measurement-by-date.use-case';
import { WeightMeasurement } from '../../domain/weight-measurement';
import { Weight } from '../../domain/weight';

export class GetWeightMeasurementByDateService implements GetWeightMeasurementByDateUseCase {
  constructor(private readonly store: WeightMeasurementStore) {}

  async get(query: GetWeightMeasurementByDateQuery): Promise<GetWeightMeasurementByDateResult> {
    if (!query) {
      throw new Error('query must not be null');
    }

    const domain = queryToDomain(query);

    const criteria = toCriteria(domain);
    const outcome = await this.store.getByDate(criteria);

    if (outcome.kind === 'failed') {
      throw new Error('Failed to get weight measurement: ' + outcome.outcomeData.errorMessage);
    }

    if (outcome.kind === 'found') {
      const domainResult = outcomeDataToDomain(outcome.outcomeData);

      return {
        kind: 'found',
        resultData: toResultData(domainResult),
      };
    }

    return { kind: 'not-found' };
  }
}

function queryToDomain(query: GetWeightMeasurementByDateQuery): CalendarDate {
  return calendarDate(query.date);
}

function outcomeDataToDomain(command: GetWeightMeasurementByDateOutcomeData): WeightMeasurement {
  return WeightMeasurement.create(calendarDate(command.date), Weight.of(command.weightInKg));
}

function toCriteria(domain: CalendarDate): GetWeightMeasurementByDateCriteria {
  return {
    date: domain,
  };
}

function toResultData(domain: WeightMeasurement): GetWeightMeasurementByDateResultData {
  return {
    date: domain.calendarDate,
    weightInKg: domain.weightInKilograms(),
  };
}
