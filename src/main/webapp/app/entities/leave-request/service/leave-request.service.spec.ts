import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ILeaveRequest } from '../leave-request.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../leave-request.test-samples';

import { LeaveRequestService, RestLeaveRequest } from './leave-request.service';

const requireRestSample: RestLeaveRequest = {
  ...sampleWithRequiredData,
  fromDate: sampleWithRequiredData.fromDate?.format(DATE_FORMAT),
  toDate: sampleWithRequiredData.toDate?.format(DATE_FORMAT),
  changedAt: sampleWithRequiredData.changedAt?.toJSON(),
};

describe('LeaveRequest Service', () => {
  let service: LeaveRequestService;
  let httpMock: HttpTestingController;
  let expectedResult: ILeaveRequest | ILeaveRequest[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LeaveRequestService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a LeaveRequest', () => {
      const leaveRequest = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(leaveRequest).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LeaveRequest', () => {
      const leaveRequest = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(leaveRequest).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LeaveRequest', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LeaveRequest', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a LeaveRequest', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addLeaveRequestToCollectionIfMissing', () => {
      it('should add a LeaveRequest to an empty array', () => {
        const leaveRequest: ILeaveRequest = sampleWithRequiredData;
        expectedResult = service.addLeaveRequestToCollectionIfMissing([], leaveRequest);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(leaveRequest);
      });

      it('should not add a LeaveRequest to an array that contains it', () => {
        const leaveRequest: ILeaveRequest = sampleWithRequiredData;
        const leaveRequestCollection: ILeaveRequest[] = [
          {
            ...leaveRequest,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLeaveRequestToCollectionIfMissing(leaveRequestCollection, leaveRequest);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LeaveRequest to an array that doesn't contain it", () => {
        const leaveRequest: ILeaveRequest = sampleWithRequiredData;
        const leaveRequestCollection: ILeaveRequest[] = [sampleWithPartialData];
        expectedResult = service.addLeaveRequestToCollectionIfMissing(leaveRequestCollection, leaveRequest);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(leaveRequest);
      });

      it('should add only unique LeaveRequest to an array', () => {
        const leaveRequestArray: ILeaveRequest[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const leaveRequestCollection: ILeaveRequest[] = [sampleWithRequiredData];
        expectedResult = service.addLeaveRequestToCollectionIfMissing(leaveRequestCollection, ...leaveRequestArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const leaveRequest: ILeaveRequest = sampleWithRequiredData;
        const leaveRequest2: ILeaveRequest = sampleWithPartialData;
        expectedResult = service.addLeaveRequestToCollectionIfMissing([], leaveRequest, leaveRequest2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(leaveRequest);
        expect(expectedResult).toContain(leaveRequest2);
      });

      it('should accept null and undefined values', () => {
        const leaveRequest: ILeaveRequest = sampleWithRequiredData;
        expectedResult = service.addLeaveRequestToCollectionIfMissing([], null, leaveRequest, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(leaveRequest);
      });

      it('should return initial array if no LeaveRequest is added', () => {
        const leaveRequestCollection: ILeaveRequest[] = [sampleWithRequiredData];
        expectedResult = service.addLeaveRequestToCollectionIfMissing(leaveRequestCollection, undefined, null);
        expect(expectedResult).toEqual(leaveRequestCollection);
      });
    });

    describe('compareLeaveRequest', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLeaveRequest(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareLeaveRequest(entity1, entity2);
        const compareResult2 = service.compareLeaveRequest(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareLeaveRequest(entity1, entity2);
        const compareResult2 = service.compareLeaveRequest(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareLeaveRequest(entity1, entity2);
        const compareResult2 = service.compareLeaveRequest(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
