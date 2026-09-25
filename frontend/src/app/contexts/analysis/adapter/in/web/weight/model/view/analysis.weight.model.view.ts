export interface AnalysisWeightDateRangeView {
  readonly startDate: string;
  readonly endDate: string;
}

export interface AnalysisWeightMeasurementView {
  readonly date: string;
  readonly dayNumber: number;
  readonly weightInKg: number;
}

export interface AnalysisWeightView {
  readonly timelineStartDate: string;
  readonly range: AnalysisWeightDateRangeView;
  readonly weightMeasurements: readonly AnalysisWeightMeasurementView[];
}
