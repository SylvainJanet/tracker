import { describe, expect, it } from 'vitest';

import { isProblemDetailResponseDto } from './shared.problem-detail-response.dto';

describe('isProblemDetailResponseDto', () => {
  it('accepts and exposes a Problem Details response with extension properties', () => {
    const response: unknown = {
      type: 'https://tracker.test/problems/unavailable',
      title: 'Service unavailable',
      status: 503,
      detail: 'The service is temporarily unavailable',
      instance: '/api/daily-records/2026-08-27',
      errorId: '07916b5f-7735-4bd5-8ae3-8f2d393f52db',
    };

    expect(isProblemDetailResponseDto(response)).toBe(true);

    if (!isProblemDetailResponseDto(response)) {
      throw new Error('Expected a Problem Details response');
    }

    expect(response['errorId']).toBe('07916b5f-7735-4bd5-8ae3-8f2d393f52db');
  });

  it('accepts a partial Problem Details response', () => {
    expect(
      isProblemDetailResponseDto({
        detail: 'The service is temporarily unavailable',
      }),
    ).toBe(true);
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
    expect(isProblemDetailResponseDto(response)).toBe(false);
  });
});
