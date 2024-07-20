import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ILeaveRequest, NewLeaveRequest } from '../leave-request.model';
import { AccountService } from '../../../core/auth/account.service';
import { HttpClient } from '@angular/common/http';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILeaveRequest for edit and NewLeaveRequestFormGroupInput for create.
 */
type LeaveRequestFormGroupInput = ILeaveRequest | PartialWithRequiredKeyOf<NewLeaveRequest>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ILeaveRequest | NewLeaveRequest> = Omit<T, 'changedAt'> & {
  changedAt?: string | null;
};

type LeaveRequestFormRawValue = FormValueOf<ILeaveRequest>;

type NewLeaveRequestFormRawValue = FormValueOf<NewLeaveRequest>;

type LeaveRequestFormDefaults = Pick<NewLeaveRequest, 'id' | 'changedAt'>;

type LeaveRequestFormGroupContent = {
  id: FormControl<LeaveRequestFormRawValue['id'] | NewLeaveRequest['id']>;
  title: FormControl<LeaveRequestFormRawValue['title']>;
  description: FormControl<LeaveRequestFormRawValue['description']>;
  fromDate: FormControl<LeaveRequestFormRawValue['fromDate']>;
  toDate: FormControl<LeaveRequestFormRawValue['toDate']>;
  status: FormControl<LeaveRequestFormRawValue['status']>;
  department: FormControl<LeaveRequestFormRawValue['department']>;
  changedAt: FormControl<LeaveRequestFormRawValue['changedAt']>;
  employee: FormControl<LeaveRequestFormRawValue['employee']>;
};

export type LeaveRequestFormGroup = FormGroup<LeaveRequestFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LeaveRequestFormService {
  constructor(
    private http: HttpClient,
    private accountService: AccountService,
  ) {}

  createLeaveRequestFormGroup(leaveRequest: LeaveRequestFormGroupInput = { id: null }): LeaveRequestFormGroup {
    const leaveRequestRawValue = this.convertLeaveRequestToLeaveRequestRawValue({
      ...this.getFormDefaults(),
      ...leaveRequest,
    });
    const isAdmin = this.accountService.hasAnyAuthority('ROLE_ADMIN');

    return new FormGroup<LeaveRequestFormGroupContent>({
      id: new FormControl(
        { value: leaveRequestRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(leaveRequestRawValue.title, {
        validators: [Validators.required],
      }),
      description: new FormControl(leaveRequestRawValue.description),
      fromDate: new FormControl(leaveRequestRawValue.fromDate, {
        validators: [Validators.required],
      }),
      toDate: new FormControl(leaveRequestRawValue.toDate, {
        validators: [Validators.required],
      }),
      status: new FormControl(
        { value: isAdmin ? leaveRequestRawValue.status : 'REQUESTED', disabled: !isAdmin },
        {
          validators: [Validators.required],
        },
      ),
      department: new FormControl(leaveRequestRawValue.department, {
        validators: [Validators.required],
      }),
      changedAt: new FormControl(leaveRequestRawValue.changedAt, {
        validators: [Validators.required],
      }),
      employee: new FormControl(leaveRequestRawValue.employee),
    });
  }

  getLeaveRequest(form: LeaveRequestFormGroup): ILeaveRequest | NewLeaveRequest {
    return this.convertLeaveRequestRawValueToLeaveRequest(form.getRawValue() as LeaveRequestFormRawValue | NewLeaveRequestFormRawValue);
  }

  resetForm(form: LeaveRequestFormGroup, leaveRequest: LeaveRequestFormGroupInput): void {
    const leaveRequestRawValue = this.convertLeaveRequestToLeaveRequestRawValue({ ...this.getFormDefaults(), ...leaveRequest });
    form.reset(
      {
        ...leaveRequestRawValue,
        id: { value: leaveRequestRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): LeaveRequestFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      changedAt: currentTime,
    };
  }

  private convertLeaveRequestRawValueToLeaveRequest(
    rawLeaveRequest: LeaveRequestFormRawValue | NewLeaveRequestFormRawValue,
  ): ILeaveRequest | NewLeaveRequest {
    return {
      ...rawLeaveRequest,
      changedAt: dayjs(rawLeaveRequest.changedAt, DATE_TIME_FORMAT),
    };
  }

  private convertLeaveRequestToLeaveRequestRawValue(
    leaveRequest: ILeaveRequest | (Partial<NewLeaveRequest> & LeaveRequestFormDefaults),
  ): LeaveRequestFormRawValue | PartialWithRequiredKeyOf<NewLeaveRequestFormRawValue> {
    return {
      ...leaveRequest,
      changedAt: leaveRequest.changedAt ? leaveRequest.changedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
