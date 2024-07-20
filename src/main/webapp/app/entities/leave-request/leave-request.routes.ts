import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { LeaveRequestComponent } from './list/leave-request.component';
import { LeaveRequestDetailComponent } from './detail/leave-request-detail.component';
import { LeaveRequestUpdateComponent } from './update/leave-request-update.component';
import LeaveRequestResolve from './route/leave-request-routing-resolve.service';

const leaveRequestRoute: Routes = [
  {
    path: '',
    component: LeaveRequestComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LeaveRequestDetailComponent,
    resolve: {
      leaveRequest: LeaveRequestResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LeaveRequestUpdateComponent,
    resolve: {
      leaveRequest: LeaveRequestResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LeaveRequestUpdateComponent,
    resolve: {
      leaveRequest: LeaveRequestResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default leaveRequestRoute;
