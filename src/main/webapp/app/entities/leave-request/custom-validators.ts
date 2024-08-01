import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import dayjs from 'dayjs';

export function fromDateBeforeToDateValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const fromDate = control.get('fromDate')?.value;
    const toDate = control.get('toDate')?.value;
    // const today = dayjs().startOf('day');

    if (fromDate && toDate && fromDate > toDate) {
      return { fromDateBeforeToDate: true };
    }
    /* if (fromDate && dayjs(fromDate).isBefore(today)) {
        return { fromDateNotBeforeToday: true };
      }*/

    return null;
  };
}
