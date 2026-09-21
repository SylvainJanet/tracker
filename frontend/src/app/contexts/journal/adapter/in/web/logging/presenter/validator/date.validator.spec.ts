import { FormControl, type ValidationErrors } from '@angular/forms';
import { describe, expect, it } from 'vitest';

import { validCalendarDate } from './date.validator';

describe('validCalendarDate', () => {
  it.each([null, undefined, ''])('leaves the empty value %s to the required validator', (value) => {
    expect(validate(value)).toBeNull();
  });

  it('accepts a valid calendar date', () => {
    expect(validate('2026-09-20')).toBeNull();
  });

  it('rejects an impossible calendar date', () => {
    expect(validate('2026-02-31')).toEqual({
      notACalendarDate: true,
    });
  });

  it('rejects a malformed calendar date', () => {
    expect(validate('not-a-date')).toEqual({
      notACalendarDate: true,
    });
  });

  it('rejects a non-string value', () => {
    expect(validate(20260920)).toEqual({
      notACalendarDate: true,
    });
  });
});

function validate(value: unknown): ValidationErrors | null {
  return validCalendarDate(new FormControl<unknown>(value));
}
