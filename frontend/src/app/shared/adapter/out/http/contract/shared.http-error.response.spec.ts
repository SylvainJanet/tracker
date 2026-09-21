import { describe, expect, it } from 'vitest';
import { gatewayErrorMessage } from './shared.http-error.response';
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

  it.each([
    null,
    'not an object',
    [],
    { type: 1 },
    { title: false },
    { status: '503' },
    { detail: { message: 'nested' } },
    { instance: 42 },
  ])('rejects a malformed Problem Details response', (response) => {
    expect(gatewayErrorMessage(response)).toBeUndefined();
  });
});
