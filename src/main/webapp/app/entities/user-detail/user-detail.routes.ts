import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import UserDetailResolve from './route/user-detail-routing-resolve.service';

const userDetailRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/user-detail').then(m => m.UserDetail),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/user-detail-detail').then(m => m.UserDetailDetail),
    resolve: {
      userDetail: UserDetailResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/user-detail-update').then(m => m.UserDetailUpdate),
    resolve: {
      userDetail: UserDetailResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/user-detail-update').then(m => m.UserDetailUpdate),
    resolve: {
      userDetail: UserDetailResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default userDetailRoute;
