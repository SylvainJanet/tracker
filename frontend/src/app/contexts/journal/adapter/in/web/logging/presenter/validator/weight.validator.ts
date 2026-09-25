import { type AbstractControl, type ValidationErrors, type ValidatorFn } from '@angular/forms';
import { Weight } from '../../../../../../../../shared/api/shared.weight-measurement';
import { exhaustiveSwitchCheck } from '../../../../../../../../shared/typescript/switch-exhaustivity-type-checking';

export const validWeight: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const value: unknown = control.value;

  // Let Validators.required handle an empty value.
  if (value === null || value === '' || value === undefined) {
    return null;
  }

  if (typeof value !== 'number' && typeof value !== 'string') {
    return { notANumber: true };
  }

  const valueAsNumber = Number(value);

  const validationErrors = Weight.validateWeightInKilograms(valueAsNumber);

  if (validationErrors.length === 0) {
    return null;
  }

  const angularErrors: ValidationErrors = {};

  for (const error of validationErrors) {
    switch (error.kind) {
      case 'number':
        angularErrors['notANumber'] = true;
        break;

      case 'positive':
        angularErrors['notPositive'] = true;
        break;

      case 'multiple-of-grams-unit':
        angularErrors['notMultipleOfGramsUnit'] = {
          gramsUnit: error.gramsUnit,
        };
        break;

      default:
        exhaustiveSwitchCheck(error);
    }
  }

  return angularErrors;
};
