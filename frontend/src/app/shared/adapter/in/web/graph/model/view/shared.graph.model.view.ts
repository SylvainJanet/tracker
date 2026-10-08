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

export class SharedGraphView {
  constructor(
    readonly accessibleDescription: string,
    readonly series: readonly SharedGraphSeriesView[],
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
