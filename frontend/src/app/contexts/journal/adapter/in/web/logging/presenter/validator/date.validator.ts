import { type AbstractControl, type ValidationErrors, type ValidatorFn } from '@angular/forms';
import { isCalendarDate } from '../../../../../../../../shared/api/shared.calendar';

export const validCalendarDate: ValidatorFn = (
  control: AbstractControl,
): ValidationErrors | null => {
  const value: unknown = control.value;

  // Let Validators.required handle an empty value.
  if (value === null || value === '' || value === undefined) {
    return null;
  }

  if (typeof value !== 'string' || !isCalendarDate(value)) {
    return { notACalendarDate: true };
  }

  return null;
};
