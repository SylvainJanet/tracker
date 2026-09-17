import { HttpErrorResponse } from '@angular/common/http';
import { describe, expect, it } from 'vitest';

import { translateCreateDailyRecordHttpError } from './journal-daily-record-http-error.translator';
import { JournalDailyRecordUnexpectedGatewayError } from '../journal-daily-record-unexpected-gateway.error';

describe('daily record HTTP error translator', () => {
  describe('translateCreateDailyRecordHttpError', () => {
    it('translates a create 409 into already exists', () => {
      const error = httpError(409);

      expect(translateCreateDailyRecordHttpError(error)).toEqual({
        kind: 'already-exists',
      });
    });

    it('does not interpret a create 404 as a conflict', () => {
      const error = httpError(404);

      expect(() => translateCreateDailyRecordHttpError(error)).toThrow(
        JournalDailyRecordUnexpectedGatewayError,
      );
    });

    it('preserves an unexpected HTTP error as its cause', () => {
      const cause = httpError(503);

      expect(() => translateCreateDailyRecordHttpError(cause)).toThrow(
        expect.objectContaining({
          name: 'JournalDailyRecordUnexpectedGatewayError',
          message: 'The daily record request failed',
          cause,
        }),
      );
    });

    it('falls back safely when an unexpected HTTP error has a malformed object body', () => {
      const cause = httpError(503, {
        detail: {
          confidentialPayload: 'must not appear in diagnostics',
        },
      });

      expect(() => translateCreateDailyRecordHttpError(cause)).toThrow(
        expect.objectContaining({
          name: 'JournalDailyRecordUnexpectedGatewayError',
          message: 'The daily record request failed',
          cause,
        }) satisfies Partial<JournalDailyRecordUnexpectedGatewayError>,
      );
    });

    it('wraps non-HTTP failures too', () => {
      const cause = new TypeError('Invalid response');

      expect(() => translateCreateDailyRecordHttpError(cause)).toThrow(
        expect.objectContaining({
          name: 'JournalDailyRecordUnexpectedGatewayError',
          message: 'The daily record request failed',
          cause,
        }),
      );
    });
  });
});

function httpError(
  status: number,
  error: unknown = {
    title: 'Request failed',
    status,
  },
): HttpErrorResponse {
  return new HttpErrorResponse({
    status,
    error,
  });
}
