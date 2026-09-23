import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import UserProfileResolve from './route/user-profile-routing-resolve.service';

const userProfileRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/user-profile').then(m => m.UserProfile),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/user-profile-detail').then(m => m.UserProfileDetail),
    resolve: {
      userProfile: UserProfileResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/user-profile-update').then(m => m.UserProfileUpdate),
    resolve: {
      userProfile: UserProfileResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/user-profile-update').then(m => m.UserProfileUpdate),
    resolve: {
      userProfile: UserProfileResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default userProfileRoute;
