export interface GetWeightMeasurementByDateResponse {
  readonly date: string;
  readonly weightInKg: number;
}

export function isGetWeightMeasurementByDateResponse(
  response: unknown,
): response is GetWeightMeasurementByDateResponse {
  if (typeof response !== 'object' || response === null || Array.isArray(response)) {
    return false;
  }

  const candidate = response as Record<string, unknown>;

  return (
    typeof candidate['date'] === 'string' &&
    typeof candidate['weightInKg'] === 'number' &&
    Number.isFinite(candidate['weightInKg'])
  );
}
