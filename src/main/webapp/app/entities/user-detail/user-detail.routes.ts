import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import UserDetailResolve from './route/user-detail-routing-resolve.service';

const userDetailRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/user-detail').then(m => m.UserDetail),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/user-detail-detail').then(m => m.UserDetailDetail),
    resolve: {
      userDetail: UserDetailResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/user-detail-update').then(m => m.UserDetailUpdate),
    resolve: {
      userDetail: UserDetailResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/user-detail-update').then(m => m.UserDetailUpdate),
    resolve: {
      userDetail: UserDetailResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default userDetailRoute;
