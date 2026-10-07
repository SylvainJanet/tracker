import type {
  SharedGraphView,
  SharedRollingAverageGraphTooltipView,
} from '../../../../../../../../shared/api/shared.graph';

export interface AnalysisWeightDateRangeView {
  readonly startDate: string;
  readonly endDate: string;
}

export interface AnalysisWeightMeasurementView {
  readonly date: string;
  readonly dayNumber: number;
  readonly weightInKg: number;
}

export interface AnalysisWeightRollingAveragePointView {
  readonly date: string;
  readonly dayNumber: number;
  readonly includedMeasurementCount: number;
  readonly averageWeightInKgApproximation: number;
  readonly completeCalendarWindow: boolean;
}

export interface AnalysisWeightRollingAverageView {
  readonly windowInDays: number;
  readonly points: readonly AnalysisWeightRollingAveragePointView[];
}

export interface AnalysisWeightView {
  readonly timelineStartDate: string;
  readonly range: AnalysisWeightDateRangeView;
  readonly weightMeasurements: readonly AnalysisWeightMeasurementView[];
  readonly rollingAverages: readonly AnalysisWeightRollingAverageView[];
  readonly graphTooltipByDayNumber: Readonly<Record<number, SharedRollingAverageGraphTooltipView>>;
  readonly graph: SharedGraphView;
}
