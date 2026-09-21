export interface LogWeightMeasurementInstruction {
  readonly date: string;
  readonly weightInKg: number;
}

export interface LogWeightMeasurementOutcomeData {
  readonly date: string;
  readonly weightInKg: number;
}

export interface LogWeightMeasurementFailedOutcomeData {
  readonly errorMessage: string;
}

export type LogWeightMeasurementOutcome =
  | {
      readonly kind: 'logged';
      readonly outcomeData: LogWeightMeasurementOutcomeData;
    }
  | {
      readonly kind: 'failed';
      readonly outcomeData: LogWeightMeasurementFailedOutcomeData;
    };

export interface LogWeightMeasurementStore {
  log(instruction: LogWeightMeasurementInstruction): Promise<LogWeightMeasurementOutcome>;
}
