export type RollingAverageRoundingOutcomeData = 'PRETTY' | 'PRECISE';

export interface RollingAverageFractionOutcomeData {
  readonly numerator: number;
  readonly denominator: number;
}

export interface RollingAverageApproximationOutcomeData {
  readonly value: number;
  readonly rounding: RollingAverageRoundingOutcomeData;
}

export interface RollingAverageValueOutcomeData {
  readonly exactValue: RollingAverageFractionOutcomeData;
  readonly approximations: readonly RollingAverageApproximationOutcomeData[];
}

export interface RollingAveragePointOutcomeData {
  readonly date: string;
  readonly dayNumber: number;
  readonly includedValues: readonly WeightMeasurementOutcomeData[];
  readonly rollingAverage: RollingAverageValueOutcomeData;
}

export interface RollingAverageOutcomeData {
  readonly windowInDays: number;
  readonly points: readonly RollingAveragePointOutcomeData[];
}

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
  readonly rollingAverages: readonly RollingAverageOutcomeData[];
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
