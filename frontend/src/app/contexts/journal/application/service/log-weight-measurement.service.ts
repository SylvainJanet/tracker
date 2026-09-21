import type {
  LogWeightMeasurementCommand,
  LogWeightMeasurementResult,
  LogWeightMeasurementResultData,
  LogWeightMeasurementUseCase,
} from '../port/in/log-weight-measurement.use-case';
import type {
  LogWeightMeasurementInstruction,
  LogWeightMeasurementOutcomeData,
  LogWeightMeasurementStore,
} from '../port/out/log-weight-measurement.store';
import { WeightMeasurement } from '../../domain/weight-measurement';
import { calendarDate } from '../../domain/calendar-date';
import { Weight } from '../../domain/weight';

export class LogWeightMeasurementService implements LogWeightMeasurementUseCase {
  constructor(private readonly store: LogWeightMeasurementStore) {}

  async log(command: LogWeightMeasurementCommand): Promise<LogWeightMeasurementResult> {
    if (!command) {
      throw new Error('command must not be null');
    }

    const domain = toDomain(command);

    const instruction = toInstruction(domain);
    const outcome = await this.store.log(instruction);

    if (outcome.kind === 'failed') {
      throw new Error('Failed to log weight measurement: ' + outcome.outcomeData.errorMessage);
    }

    return {
      kind: 'logged',
      resultData: toResultData(outcome.outcomeData),
    };
  }
}

function toDomain(command: LogWeightMeasurementCommand): WeightMeasurement {
  return WeightMeasurement.create(calendarDate(command.date), Weight.of(command.weightInKg));
}

function toInstruction(domain: WeightMeasurement): LogWeightMeasurementInstruction {
  return {
    date: domain.calendarDate,
    weightInKg: domain.weightInKilograms(),
  };
}

function toResultData(
  outcomeData: LogWeightMeasurementOutcomeData,
): LogWeightMeasurementResultData {
  return {
    date: outcomeData.date,
    weightInKg: outcomeData.weightInKg,
  };
}
