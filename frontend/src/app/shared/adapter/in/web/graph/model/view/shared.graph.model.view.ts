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
