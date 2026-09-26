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
