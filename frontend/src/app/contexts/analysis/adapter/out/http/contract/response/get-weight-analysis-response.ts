export interface WeightAnalysisDateRangeResponse {
  readonly startDate: string;
  readonly endDate: string;
}

export interface WeightMeasurementResponse {
  readonly date: string;
  readonly dayNumber: number;
  readonly weightInKg: number;
}

export interface RollingAveragePointResponse {
  readonly date: string;
  readonly dayNumber: number;
  readonly averageWeightInKg: number;
  readonly includedMeasurementCount: number;
  readonly completeCalendarWindow: boolean;
}

export interface RollingAverageResponse {
  readonly windowInDays: number;
  readonly points: readonly RollingAveragePointResponse[];
}

export interface GetWeightAnalysisResponse {
  readonly timelineStartDate: string | null;
  readonly range: WeightAnalysisDateRangeResponse | null;
  readonly weightMeasurements: readonly WeightMeasurementResponse[];
  readonly rollingAverages: readonly RollingAverageResponse[];
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
  const rollingAverages = response['rollingAverages'];

  if (
    !Array.isArray(weightMeasurements) ||
    !weightMeasurements.every(isWeightMeasurementResponse) ||
    !Array.isArray(rollingAverages) ||
    rollingAverages.length > 0
  ) {
    return false;
  }

  if (timelineStartDate === null || range === null) {
    return timelineStartDate === null && range === null && weightMeasurements.length === 0;
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

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null && !Array.isArray(value);
}
