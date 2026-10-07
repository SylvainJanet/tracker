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
