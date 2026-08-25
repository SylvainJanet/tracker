import { HttpErrorResponse } from '@angular/common/http';

import {
  type CreateDailyRecordOutcome,
  type FindDailyRecordOutcome,
} from '../../../../../application/port/out/daily-record.gateway';
import { DailyRecordUnexpectedGatewayError } from '../daily-record-unexpected-gateway.error';
import { DailyRecordHttpContractMapper } from '../../contract/daily-record-http-contract.mapper';

export function translateGetDailyRecordHttpError(error: unknown): FindDailyRecordOutcome {
  if (!isHttpError(error)) {
    throw unexpectedGatewayError(error);
  }

  if (hasHttpStatus(error, 404)) {
    return {
      kind: 'not-found',
    };
  }

  throw unexpectedGatewayError(error);
}

export function translateCreateDailyRecordHttpError(error: unknown): CreateDailyRecordOutcome {
  if (!isHttpError(error)) {
    throw unexpectedGatewayError(error);
  }

  if (hasHttpStatus(error, 409)) {
    return {
      kind: 'already-exists',
    };
  }

  throw unexpectedGatewayError(error);
}

function unexpectedGatewayError(cause: unknown): DailyRecordUnexpectedGatewayError {
  const backendMessage = isHttpError(cause)
    ? DailyRecordHttpContractMapper.problemDetailMessage(cause.error)
    : undefined;

  return new DailyRecordUnexpectedGatewayError(
    backendMessage ?? 'The daily record request failed',
    { cause },
  );
}

function isHttpError(error: unknown): error is HttpErrorResponse {
  return error instanceof HttpErrorResponse;
}

function hasHttpStatus(error: unknown, expectedStatus: number): boolean {
  return isHttpError(error) && error.status === expectedStatus;
}
