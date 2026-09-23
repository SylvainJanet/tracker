export interface GetWeightMeasurementByDateQuery {
  readonly date: string;
}

export interface GetWeightMeasurementByDateResultData {
  readonly date: string;
  readonly weightInKg: number;
}

export type GetWeightMeasurementByDateResult =
  | {
      readonly kind: 'found';
      readonly resultData: GetWeightMeasurementByDateResultData;
    }
  | {
      readonly kind: 'not-found';
    };

export interface GetWeightMeasurementByDateUseCase {
  get(command: GetWeightMeasurementByDateQuery): Promise<GetWeightMeasurementByDateResult>;
}
