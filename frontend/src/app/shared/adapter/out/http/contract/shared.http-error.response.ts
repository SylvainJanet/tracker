import { HttpErrorResponse } from '@angular/common/http';

interface ProblemDetailResponse {
  readonly [extension: string]: unknown;
  readonly type?: string;
  readonly title?: string;
  readonly status?: number;
  readonly detail?: string;
  readonly instance?: string;
}

function isProblemDetailResponse(response: unknown): response is ProblemDetailResponse {
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

function problemDetailMessage(response: unknown): string | undefined {
  if (!isProblemDetailResponse(response)) {
    return undefined;
  }

  return response.detail;
}

function isHttpError(error: unknown): error is HttpErrorResponse {
  return error instanceof HttpErrorResponse;
}

export function gatewayErrorMessage(error: unknown): string | undefined {
  if (!isHttpError(error)) {
    return undefined;
  }

  return problemDetailMessage(error.error);
}
