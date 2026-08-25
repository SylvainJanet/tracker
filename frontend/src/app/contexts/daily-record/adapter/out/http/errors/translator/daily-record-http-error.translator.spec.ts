import { HttpErrorResponse } from '@angular/common/http';
import { describe, expect, it } from 'vitest';

import {
  translateCreateDailyRecordHttpError,
  translateGetDailyRecordHttpError,
} from './daily-record-http-error.translator';
import { DailyRecordUnexpectedGatewayError } from '../daily-record-unexpected-gateway.error';

describe('daily record HTTP error translator', () => {
  describe('translateGetDailyRecordHttpError', () => {
    it('translates a get 404 into an absent record', () => {
      const error = httpError(404);

      expect(translateGetDailyRecordHttpError(error)).toEqual({
        kind: 'not-found',
      });
    });

    it('does not interpret a get 409 as not found', () => {
      const error = httpError(409);

      expect(() => translateGetDailyRecordHttpError(error)).toThrow(
        DailyRecordUnexpectedGatewayError,
      );
    });

    it('preserves an unexpected HTTP error as its cause', () => {
      const cause = httpError(503);

      expect(() => translateGetDailyRecordHttpError(cause)).toThrow(
        expect.objectContaining({
          name: 'DailyRecordUnexpectedGatewayError',
          message: 'The daily record request failed',
          cause,
        }),
      );
    });

    it('falls back safely when an unexpected HTTP error has a plain-text body', () => {
      const cause = httpError(503, 'confidential upstream response');

      expect(() => translateGetDailyRecordHttpError(cause)).toThrow(
        expect.objectContaining({
          name: 'DailyRecordUnexpectedGatewayError',
          message: 'The daily record request failed',
          cause,
        }) satisfies Partial<DailyRecordUnexpectedGatewayError>,
      );
    });

    it('uses a valid problem detail as a safe diagnostic message', () => {
      const cause = httpError(503, {
        title: 'Service unavailable',
        status: 503,
        detail: 'Daily records are temporarily unavailable',
      });

      expect(() => translateGetDailyRecordHttpError(cause)).toThrow(
        expect.objectContaining({
          name: 'DailyRecordUnexpectedGatewayError',
          message: 'Daily records are temporarily unavailable',
          cause,
        }) satisfies Partial<DailyRecordUnexpectedGatewayError>,
      );
    });

    it('wraps non-HTTP failures too', () => {
      const cause = new TypeError('Invalid response');

      expect(() => translateGetDailyRecordHttpError(cause)).toThrow(
        expect.objectContaining({
          name: 'DailyRecordUnexpectedGatewayError',
          message: 'The daily record request failed',
          cause,
        }),
      );
    });
  });

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
        DailyRecordUnexpectedGatewayError,
      );
    });

    it('preserves an unexpected HTTP error as its cause', () => {
      const cause = httpError(503);

      expect(() => translateCreateDailyRecordHttpError(cause)).toThrow(
        expect.objectContaining({
          name: 'DailyRecordUnexpectedGatewayError',
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
          name: 'DailyRecordUnexpectedGatewayError',
          message: 'The daily record request failed',
          cause,
        }) satisfies Partial<DailyRecordUnexpectedGatewayError>,
      );
    });

    it('wraps non-HTTP failures too', () => {
      const cause = new TypeError('Invalid response');

      expect(() => translateCreateDailyRecordHttpError(cause)).toThrow(
        expect.objectContaining({
          name: 'DailyRecordUnexpectedGatewayError',
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
