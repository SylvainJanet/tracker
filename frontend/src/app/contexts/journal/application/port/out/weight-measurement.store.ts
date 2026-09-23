export interface LogWeightMeasurementInstruction {
  readonly date: string;
  readonly weightInKg: number;
}

export interface GetWeightMeasurementByDateCriteria {
  readonly date: string;
}

export interface LogWeightMeasurementOutcomeData {
  readonly date: string;
  readonly weightInKg: number;
}

export interface LogWeightMeasurementFailedOutcomeData {
  readonly errorMessage: string;
}

export interface GetWeightMeasurementByDateOutcomeData {
  readonly date: string;
  readonly weightInKg: number;
}

export interface GetWeightMeasurementByDateFailedOutcomeData {
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

export type GetWeightMeasurementByDateOutcome =
  | {
      readonly kind: 'found';
      readonly outcomeData: GetWeightMeasurementByDateOutcomeData;
    }
  | {
      readonly kind: 'not-found';
    }
  | {
      readonly kind: 'failed';
      readonly outcomeData: GetWeightMeasurementByDateFailedOutcomeData;
    };

export interface WeightMeasurementStore {
  log(instruction: LogWeightMeasurementInstruction): Promise<LogWeightMeasurementOutcome>;

  getByDate(
    criteria: GetWeightMeasurementByDateCriteria,
  ): Promise<GetWeightMeasurementByDateOutcome>;
}
