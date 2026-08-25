export interface ProblemDetailResponseDto {
  readonly [extension: string]: unknown;
  readonly type?: string;
  readonly title?: string;
  readonly status?: number;
  readonly detail?: string;
  readonly instance?: string;
}

export function isProblemDetailResponseDto(
  response: unknown,
): response is ProblemDetailResponseDto {
  if (typeof response !== 'object' || response === null || Array.isArray(response)) {
    return false;
  }

  const candidate = response as Record<string, unknown>;

  return (
    (typeof candidate['type'] === 'string' || candidate['type'] === undefined) &&
    (typeof candidate['title'] === 'string' || candidate['title'] === undefined) &&
    (typeof candidate['status'] === 'number' || candidate['status'] === undefined) &&
    (typeof candidate['detail'] === 'string' || candidate['detail'] === undefined) &&
    (typeof candidate['instance'] === 'string' || candidate['instance'] === undefined)
  );
}
