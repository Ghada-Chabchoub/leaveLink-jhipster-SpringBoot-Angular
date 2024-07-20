import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { LeaveStatus } from 'app/entities/enumerations/leave-status.model';
import { Department } from 'app/entities/enumerations/department.model';
import { LeaveRequestService } from '../service/leave-request.service';
import { ILeaveRequest } from '../leave-request.model';
import { LeaveRequestFormService, LeaveRequestFormGroup } from './leave-request-form.service';
import { AccountService } from 'app/core/auth/account.service';

@Component({
  standalone: true,
  selector: 'jhi-leave-request-update',
  templateUrl: './leave-request-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class LeaveRequestUpdateComponent implements OnInit {
  isSaving = false;
  leaveRequest: ILeaveRequest | null = null;
  leaveStatusValues = Object.keys(LeaveStatus);
  departmentValues = Object.keys(Department);
  isAdmin: boolean = false;
  usersSharedCollection: IUser[] = [];

  protected leaveRequestService = inject(LeaveRequestService);
  protected leaveRequestFormService = inject(LeaveRequestFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);
  protected accountService = inject(AccountService);

  editForm: LeaveRequestFormGroup = this.leaveRequestFormService.createLeaveRequestFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    console.log('heeloo');

    this.isAdmin = this.accountService.hasAnyAuthority('ROLE_ADMIN');
    this.activatedRoute.data.subscribe(({ leaveRequest }) => {
      this.leaveRequest = leaveRequest;
      if (leaveRequest) {
        this.updateForm(leaveRequest);
      }
      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const leaveRequest = this.leaveRequestFormService.getLeaveRequest(this.editForm);
    if (leaveRequest.id !== null) {
      this.subscribeToSaveResponse(this.leaveRequestService.update(leaveRequest));
    } else {
      this.subscribeToSaveResponse(this.leaveRequestService.create(leaveRequest));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILeaveRequest>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(leaveRequest: ILeaveRequest): void {
    this.leaveRequest = leaveRequest;
    this.leaveRequestFormService.resetForm(this.editForm, leaveRequest);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, leaveRequest.employee);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.leaveRequest?.employee)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
