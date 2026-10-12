import { describe, expect, it } from 'vitest';

import { isRecord } from './is-record';

describe('isRecord', () => {
  it.each([{}, { value: 1 }, new Date()])('accepts object records', (value) => {
    expect(isRecord(value)).toBe(true);
  });

  it.each([null, undefined, [], 'value', 42, true, () => undefined])(
    'rejects non-record values',
    (value) => {
      expect(isRecord(value)).toBe(false);
    },
  );
});
