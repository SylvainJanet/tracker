import { describe, expect, it } from 'vitest';
import { gatewayErrorMessage, isHttpErrorWithStatus } from './shared.http-error.response';
import { HttpErrorResponse } from '@angular/common/http';

describe('gatewayErrorMessage', () => {
  it('accepts and exposes a Problem Details response with extension properties', () => {
    const response = new HttpErrorResponse({
      error: {
        type: 'https://tracker.test/problems/unavailable',
        title: 'Service unavailable',
        status: 503,
        detail: 'The service is temporarily unavailable',
        instance: '/api/daily-records/2026-08-27',
        errorId: '07916b5f-7735-4bd5-8ae3-8f2d393f52db',
      },
    });

    expect(gatewayErrorMessage(response)).toBe('The service is temporarily unavailable');
  });

  it('accepts a partial Problem Details response', () => {
    expect(
      gatewayErrorMessage(
        new HttpErrorResponse({
          error: {
            detail: 'The service is temporarily unavailable',
          },
        }),
      ),
    ).toBe('The service is temporarily unavailable');
  });

  it('uses the fallback message for a valid Problem Details response without detail', () => {
    const error = new HttpErrorResponse({
      error: {
        title: 'Service unavailable',
        status: 503,
      },
    });

    expect(gatewayErrorMessage(error)).toBe('An unexpected gateway error occurred.');
  });

  it('uses the fallback message for a non-HTTP error', () => {
    expect(gatewayErrorMessage(new Error('Unexpected failure'))).toBe(
      'An unexpected gateway error occurred.',
    );
  });

  it.each([
    null,
    'not an object',
    [],
    { type: 1 },
    { title: false },
    { status: '503' },
    { detail: { message: 'nested' } },
    { instance: 42 },
  ])('uses the fallback message for a malformed Problem Details response', (response) => {
    const error = new HttpErrorResponse({
      error: response,
    });

    expect(gatewayErrorMessage(error)).toBe('An unexpected gateway error occurred.');
  });
});

describe('isHttpErrorWithStatus', () => {
  it('accepts an HTTP error with the requested status', () => {
    const error = new HttpErrorResponse({
      status: 404,
      statusText: 'Not Found',
    });

    expect(isHttpErrorWithStatus(error, 404)).toBe(true);
  });

  it('rejects an HTTP error with another status', () => {
    const error = new HttpErrorResponse({
      status: 500,
      statusText: 'Internal Server Error',
    });

    expect(isHttpErrorWithStatus(error, 404)).toBe(false);
  });

  it('rejects a non-HTTP error', () => {
    expect(isHttpErrorWithStatus(new Error('Unexpected failure'), 404)).toBe(false);
  });
});
