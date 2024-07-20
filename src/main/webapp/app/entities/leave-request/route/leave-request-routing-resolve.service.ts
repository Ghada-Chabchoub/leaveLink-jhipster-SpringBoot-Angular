import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILeaveRequest } from '../leave-request.model';
import { LeaveRequestService } from '../service/leave-request.service';

const leaveRequestResolve = (route: ActivatedRouteSnapshot): Observable<null | ILeaveRequest> => {
  const id = route.params['id'];
  if (id) {
    return inject(LeaveRequestService)
      .find(id)
      .pipe(
        mergeMap((leaveRequest: HttpResponse<ILeaveRequest>) => {
          if (leaveRequest.body) {
            return of(leaveRequest.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default leaveRequestResolve;
