export type RollingAverageRoundingResultData = 'PRETTY' | 'PRECISE';

export interface RollingAverageFractionResultData {
  readonly numerator: number;
  readonly denominator: number;
}

export interface RollingAverageApproximationResultData {
  readonly value: number;
  readonly rounding: RollingAverageRoundingResultData;
}

export interface RollingAverageValueResultData {
  readonly exactValue: RollingAverageFractionResultData;
  readonly approximations: readonly RollingAverageApproximationResultData[];
}

export interface RollingAveragePointResultData {
  readonly date: string;
  readonly dayNumber: number;
  readonly includedValues: readonly WeightMeasurementResultData[];
  readonly rollingAverage: RollingAverageValueResultData;
}

export interface RollingAverageResultData {
  readonly windowInDays: number;
  readonly points: readonly RollingAveragePointResultData[];
}

export interface WeightAnalysisDateRangeResultData {
  readonly startDate: string;
  readonly endDate: string;
}

export interface WeightMeasurementResultData {
  readonly date: string;
  readonly dayNumber: number;
  readonly weightInKg: number;
}

export interface WeightAnalysisResultData {
  readonly timelineStartDate: string;
  readonly range: WeightAnalysisDateRangeResultData;
  readonly weightMeasurements: readonly WeightMeasurementResultData[];
  readonly rollingAverages: readonly RollingAverageResultData[];
}

export type GetWeightAnalysisResult =
  | {
      readonly kind: 'data';
      readonly resultData: WeightAnalysisResultData;
    }
  | {
      readonly kind: 'empty';
    };

export interface GetWeightAnalysisUseCase {
  get(): Promise<GetWeightAnalysisResult>;
}
