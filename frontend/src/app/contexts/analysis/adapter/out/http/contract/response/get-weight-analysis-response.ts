export interface WeightAnalysisDateRangeResponse {
  readonly startDate: string;
  readonly endDate: string;
}

export interface WeightMeasurementResponse {
  readonly date: string;
  readonly dayNumber: number;
  readonly weightInKg: number;
}

export interface RollingAverageFractionResponse {
  readonly numerator: number;
  readonly denominator: number;
}

export type RollingAverageRoundingResponse = 'PRETTY' | 'PRECISE';

export interface RollingAverageApproximationResponse {
  readonly value: number;
  readonly rounding: RollingAverageRoundingResponse;
}

export interface RollingAverageValueResponse {
  readonly exactValue: RollingAverageFractionResponse;
  readonly approximations: readonly RollingAverageApproximationResponse[];
}

export interface RollingAveragePointResponse {
  readonly date: string;
  readonly dayNumber: number;
  readonly includedValues: readonly WeightMeasurementResponse[];
  readonly rollingAverage: RollingAverageValueResponse;
}

export interface RollingAverageResponse {
  readonly windowSize: number;
  readonly rollingAverages: readonly RollingAveragePointResponse[];
}

export interface GetWeightAnalysisResponse {
  readonly timelineStartDate: string | null;
  readonly range: WeightAnalysisDateRangeResponse | null;
  readonly weightMeasurements: readonly WeightMeasurementResponse[];
  readonly rollingAverageSeries: readonly RollingAverageResponse[];
}

export function isGetWeightAnalysisResponse(
  response: unknown,
): response is GetWeightAnalysisResponse {
  if (!isRecord(response)) {
    return false;
  }

  const timelineStartDate = response['timelineStartDate'];
  const range = response['range'];
  const weightMeasurements = response['weightMeasurements'];
  const rollingAverageSeries = response['rollingAverageSeries'];

  if (
    !Array.isArray(weightMeasurements) ||
    !weightMeasurements.every(isWeightMeasurementResponse) ||
    !Array.isArray(rollingAverageSeries) ||
    !rollingAverageSeries.every(isRollingAverageResponse)
  ) {
    return false;
  }

  if (timelineStartDate === null || range === null) {
    return (
      timelineStartDate === null &&
      range === null &&
      weightMeasurements.length === 0 &&
      rollingAverageSeries.length === 0
    );
  }

  return typeof timelineStartDate === 'string' && isDateRangeResponse(range);
}

function isDateRangeResponse(response: unknown): response is WeightAnalysisDateRangeResponse {
  return (
    isRecord(response) &&
    typeof response['startDate'] === 'string' &&
    typeof response['endDate'] === 'string'
  );
}

function isWeightMeasurementResponse(response: unknown): response is WeightMeasurementResponse {
  return (
    isRecord(response) &&
    typeof response['date'] === 'string' &&
    typeof response['dayNumber'] === 'number' &&
    typeof response['weightInKg'] === 'number'
  );
}

function isRollingAverageResponse(response: unknown): response is RollingAverageResponse {
  return (
    isRecord(response) &&
    typeof response['windowSize'] === 'number' &&
    Array.isArray(response['rollingAverages']) &&
    response['rollingAverages'].every(isRollingAveragePointResponse)
  );
}

function isRollingAveragePointResponse(response: unknown): response is RollingAveragePointResponse {
  return (
    isRecord(response) &&
    typeof response['date'] === 'string' &&
    typeof response['dayNumber'] === 'number' &&
    Array.isArray(response['includedValues']) &&
    response['includedValues'].every(isWeightMeasurementResponse) &&
    isRollingAverageValueResponse(response['rollingAverage'])
  );
}

function isRollingAverageValueResponse(response: unknown): response is RollingAverageValueResponse {
  return (
    isRecord(response) &&
    isRollingAverageFractionResponse(response['exactValue']) &&
    Array.isArray(response['approximations']) &&
    response['approximations'].every(isRollingAverageApproximationResponse)
  );
}

function isRollingAverageFractionResponse(
  response: unknown,
): response is RollingAverageFractionResponse {
  return (
    isRecord(response) &&
    typeof response['numerator'] === 'number' &&
    typeof response['denominator'] === 'number'
  );
}

function isRollingAverageApproximationResponse(
  response: unknown,
): response is RollingAverageApproximationResponse {
  return (
    isRecord(response) &&
    typeof response['value'] === 'number' &&
    isRollingAverageRoundingResponse(response['rounding'])
  );
}

function isRollingAverageRoundingResponse(
  response: unknown,
): response is RollingAverageRoundingResponse {
  return response === 'PRETTY' || response === 'PRECISE';
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null && !Array.isArray(value);
}
