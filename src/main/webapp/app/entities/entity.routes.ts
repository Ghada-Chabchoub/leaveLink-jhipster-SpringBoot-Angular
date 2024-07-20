import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'leaveLinkApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'leave-request',
    data: { pageTitle: 'leaveLinkApp.leaveRequest.home.title' },
    loadChildren: () => import('./leave-request/leave-request.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
