export interface SharedGraphPointView {
  readonly x: number;
  readonly y: number;
}

export interface SharedGraphSeriesView {
  readonly label: string;
  readonly color: string;
  readonly points: readonly SharedGraphPointView[];
}

export class SharedGraphModel {
  constructor(
    readonly accessibleDescription: string,
    readonly series: SharedGraphSeriesView,
  ) {}
}
