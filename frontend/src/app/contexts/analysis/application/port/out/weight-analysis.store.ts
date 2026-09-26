export interface WeightAnalysisDateRangeOutcomeData {
  readonly startDate: string;
  readonly endDate: string;
}

export interface WeightMeasurementOutcomeData {
  readonly date: string;
  readonly dayNumber: number;
  readonly weightInKg: number;
}

export interface WeightAnalysisOutcomeData {
  readonly timelineStartDate: string;
  readonly range: WeightAnalysisDateRangeOutcomeData;
  readonly weightMeasurements: readonly WeightMeasurementOutcomeData[];
}

export interface WeightAnalysisFailedOutcomeData {
  readonly errorMessage: string;
}

export type GetWeightAnalysisOutcome =
  | {
      readonly kind: 'data';
      readonly outcomeData: WeightAnalysisOutcomeData;
    }
  | {
      readonly kind: 'empty';
    }
  | {
      readonly kind: 'failed';
      readonly outcomeData: WeightAnalysisFailedOutcomeData;
    };

export interface WeightAnalysisStore {
  get(): Promise<GetWeightAnalysisOutcome>;
}
