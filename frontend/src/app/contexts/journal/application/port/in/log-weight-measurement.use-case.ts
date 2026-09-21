export interface LogWeightMeasurementCommand {
  readonly date: string;
  readonly weightInKg: number;
}

export interface LogWeightMeasurementResultData {
  readonly date: string;
  readonly weightInKg: number;
}

export interface LogWeightMeasurementResult {
  readonly kind: 'logged';
  readonly resultData: LogWeightMeasurementResultData;
}

export interface LogWeightMeasurementUseCase {
  log(command: LogWeightMeasurementCommand): Promise<LogWeightMeasurementResult>;
}
