import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { LeaveRequestService } from '../service/leave-request.service';
import { ILeaveRequest } from '../leave-request.model';
import { LeaveRequestFormService } from './leave-request-form.service';

import { LeaveRequestUpdateComponent } from './leave-request-update.component';

describe('LeaveRequest Management Update Component', () => {
  let comp: LeaveRequestUpdateComponent;
  let fixture: ComponentFixture<LeaveRequestUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let leaveRequestFormService: LeaveRequestFormService;
  let leaveRequestService: LeaveRequestService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [LeaveRequestUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(LeaveRequestUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LeaveRequestUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    leaveRequestFormService = TestBed.inject(LeaveRequestFormService);
    leaveRequestService = TestBed.inject(LeaveRequestService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const leaveRequest: ILeaveRequest = { id: 456 };
      const employee: IUser = { id: 14016 };
      leaveRequest.employee = employee;

      const userCollection: IUser[] = [{ id: 2678 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [employee];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ leaveRequest });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const leaveRequest: ILeaveRequest = { id: 456 };
      const employee: IUser = { id: 4666 };
      leaveRequest.employee = employee;

      activatedRoute.data = of({ leaveRequest });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(employee);
      expect(comp.leaveRequest).toEqual(leaveRequest);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILeaveRequest>>();
      const leaveRequest = { id: 123 };
      jest.spyOn(leaveRequestFormService, 'getLeaveRequest').mockReturnValue(leaveRequest);
      jest.spyOn(leaveRequestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ leaveRequest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: leaveRequest }));
      saveSubject.complete();

      // THEN
      expect(leaveRequestFormService.getLeaveRequest).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(leaveRequestService.update).toHaveBeenCalledWith(expect.objectContaining(leaveRequest));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILeaveRequest>>();
      const leaveRequest = { id: 123 };
      jest.spyOn(leaveRequestFormService, 'getLeaveRequest').mockReturnValue({ id: null });
      jest.spyOn(leaveRequestService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ leaveRequest: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: leaveRequest }));
      saveSubject.complete();

      // THEN
      expect(leaveRequestFormService.getLeaveRequest).toHaveBeenCalled();
      expect(leaveRequestService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILeaveRequest>>();
      const leaveRequest = { id: 123 };
      jest.spyOn(leaveRequestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ leaveRequest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(leaveRequestService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
