import { HttpErrorResponse } from '@angular/common/http';

import { type CreateDailyRecordOutcome } from '../../../../../application/port/out/daily-record.gateway';
import { JournalDailyRecordHttpContractMapper } from '../../contract/journal-daily-record-http-contract.mapper';
import { JournalDailyRecordUnexpectedGatewayError } from '../journal-daily-record-unexpected-gateway.error';

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

function unexpectedGatewayError(cause: unknown): JournalDailyRecordUnexpectedGatewayError {
  const backendMessage = isHttpError(cause)
    ? JournalDailyRecordHttpContractMapper.problemDetailMessage(cause.error)
    : undefined;

  return new JournalDailyRecordUnexpectedGatewayError(
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
