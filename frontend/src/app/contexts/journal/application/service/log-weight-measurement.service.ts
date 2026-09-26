import type {
  LogWeightMeasurementCommand,
  LogWeightMeasurementResult,
  LogWeightMeasurementResultData,
  LogWeightMeasurementUseCase,
} from '../port/in/log-weight-measurement.use-case';
import type {
  LogWeightMeasurementInstruction,
  LogWeightMeasurementOutcomeData,
  WeightMeasurementStore,
} from '../port/out/weight-measurement.store';
import { calendarDate } from '../../../../shared/api/shared.calendar';
import { WeightMeasurement } from '../../domain/weight-measurement';
import { Weight } from '../../domain/weight';

export class LogWeightMeasurementService implements LogWeightMeasurementUseCase {
  constructor(private readonly store: WeightMeasurementStore) {}

  async log(command: LogWeightMeasurementCommand): Promise<LogWeightMeasurementResult> {
    if (!command) {
      throw new Error('command must not be null');
    }

    const domain = commandToDomain(command);

    const instruction = toInstruction(domain);
    const outcome = await this.store.log(instruction);

    if (outcome.kind === 'failed') {
      throw new Error('Failed to log weight measurement: ' + outcome.outcomeData.errorMessage);
    }

    const domainResponse = outcomeDataToDomain(outcome.outcomeData);

    return {
      kind: 'logged',
      resultData: toResultData(domainResponse),
    };
  }
}

function commandToDomain(command: LogWeightMeasurementCommand): WeightMeasurement {
  return WeightMeasurement.create(calendarDate(command.date), Weight.of(command.weightInKg));
}

function outcomeDataToDomain(command: LogWeightMeasurementOutcomeData): WeightMeasurement {
  return WeightMeasurement.create(calendarDate(command.date), Weight.of(command.weightInKg));
}

function toInstruction(domain: WeightMeasurement): LogWeightMeasurementInstruction {
  return {
    date: domain.calendarDate,
    weightInKg: domain.weightInKilograms(),
  };
}

function toResultData(domain: WeightMeasurement): LogWeightMeasurementResultData {
  return {
    date: domain.calendarDate,
    weightInKg: domain.weightInKilograms(),
  };
}
