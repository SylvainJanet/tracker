export interface SharedGraphPointView {
  readonly x: number;
  readonly y: number;
  readonly intensity?: number;
}

export interface SharedGraphSeriesView {
  readonly label: string;
  readonly color: string;
  readonly points: readonly SharedGraphPointView[];
}

export interface SharedGraphAxisExtentView {
  readonly minimum: number;
  readonly maximum: number;
}

export interface SharedGraphAxisIntervalView {
  readonly maximumSpanExclusive: number;
  readonly interval: number;
}

export interface SharedGraphAxisPolicyView {
  readonly kind: 'discrete' | 'continuous';
  readonly intervals: readonly SharedGraphAxisIntervalView[];
  readonly fallbackTargetLabelCount: number;
}

export interface SharedGraphAxisScaleView extends SharedGraphAxisExtentView {
  readonly interval: number;
  readonly labelValues: readonly number[];
}

export interface SharedGraphAxisView {
  readonly label: string;
  readonly formatValue: (value: number) => string;
  readonly extent?: SharedGraphAxisExtentView;
  readonly policy?: SharedGraphAxisPolicyView;
}

export interface SharedGraphPresentationView {
  readonly title: string;
  readonly xAxis: SharedGraphAxisView;
  readonly yAxis: SharedGraphAxisView;
}

export class SharedGraphView {
  constructor(
    readonly accessibleDescription: string,
    readonly series: readonly SharedGraphSeriesView[],
    readonly presentation?: SharedGraphPresentationView,
  ) {}
}

export interface SharedGraphTooltipValueView {
  readonly seriesLabel: string;
  readonly color: string;
  readonly formattedValue: string;
}

export interface SharedGraphTooltipCoverageView {
  readonly includedValueCount: number;
  readonly windowInDays: number;
}

export interface SharedGraphTooltipRollingAverageView extends SharedGraphTooltipValueView {
  readonly coverage: SharedGraphTooltipCoverageView;
}

export interface SharedRollingAverageGraphTooltipView {
  readonly heading: string;
  readonly primaryValue: SharedGraphTooltipValueView;
  readonly rollingAverages: readonly SharedGraphTooltipRollingAverageView[];
}

export interface SharedGraphRollingAverageCalculationView {
  readonly exactValue: {
    readonly numerator: number;
    readonly denominator: number;
  };
  readonly prettyApproximation: string;
  readonly preciseApproximation: string;
}

export interface SharedGraphRollingAverageTrendView {
  readonly graph: SharedGraphView;
  readonly tooltipByX: Readonly<Record<number, SharedRollingAverageGraphTooltipView>>;
}

export interface SharedGraphDialogRollingAverageView extends SharedGraphTooltipRollingAverageView {
  readonly description: string;
  readonly calculation: SharedGraphRollingAverageCalculationView;
  readonly trend: SharedGraphRollingAverageTrendView;
}

export interface SharedRollingAverageGraphDialogView {
  readonly heading: string;
  readonly primaryValue: SharedGraphTooltipValueView;
  readonly rollingAverages: readonly SharedGraphDialogRollingAverageView[];
}
