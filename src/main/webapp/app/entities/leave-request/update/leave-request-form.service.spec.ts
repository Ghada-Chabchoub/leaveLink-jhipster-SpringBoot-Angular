import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../leave-request.test-samples';

import { LeaveRequestFormService } from './leave-request-form.service';

describe('LeaveRequest Form Service', () => {
  let service: LeaveRequestFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LeaveRequestFormService);
  });

  describe('Service methods', () => {
    describe('createLeaveRequestFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLeaveRequestFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            fromDate: expect.any(Object),
            toDate: expect.any(Object),
            status: expect.any(Object),
            department: expect.any(Object),
            changedAt: expect.any(Object),
            employee: expect.any(Object),
          }),
        );
      });

      it('passing ILeaveRequest should create a new form with FormGroup', () => {
        const formGroup = service.createLeaveRequestFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            fromDate: expect.any(Object),
            toDate: expect.any(Object),
            status: expect.any(Object),
            department: expect.any(Object),
            changedAt: expect.any(Object),
            employee: expect.any(Object),
          }),
        );
      });
    });

    describe('getLeaveRequest', () => {
      it('should return NewLeaveRequest for default LeaveRequest initial value', () => {
        const formGroup = service.createLeaveRequestFormGroup(sampleWithNewData);

        const leaveRequest = service.getLeaveRequest(formGroup) as any;

        expect(leaveRequest).toMatchObject(sampleWithNewData);
      });

      it('should return NewLeaveRequest for empty LeaveRequest initial value', () => {
        const formGroup = service.createLeaveRequestFormGroup();

        const leaveRequest = service.getLeaveRequest(formGroup) as any;

        expect(leaveRequest).toMatchObject({});
      });

      it('should return ILeaveRequest', () => {
        const formGroup = service.createLeaveRequestFormGroup(sampleWithRequiredData);

        const leaveRequest = service.getLeaveRequest(formGroup) as any;

        expect(leaveRequest).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILeaveRequest should not enable id FormControl', () => {
        const formGroup = service.createLeaveRequestFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLeaveRequest should disable id FormControl', () => {
        const formGroup = service.createLeaveRequestFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
