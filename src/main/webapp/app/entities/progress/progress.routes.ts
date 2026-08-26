import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ProgressResolve from './route/progress-routing-resolve.service';

const progressRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/progress').then(m => m.Progress),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/progress-detail').then(m => m.ProgressDetail),
    resolve: {
      progress: ProgressResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/progress-update').then(m => m.ProgressUpdate),
    resolve: {
      progress: ProgressResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/progress-update').then(m => m.ProgressUpdate),
    resolve: {
      progress: ProgressResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default progressRoute;
