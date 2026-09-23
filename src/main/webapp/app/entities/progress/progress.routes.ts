import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import ProgressResolve from './route/progress-routing-resolve.service';

const progressRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/progress').then(m => m.Progress),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/progress-detail').then(m => m.ProgressDetail),
    resolve: {
      progress: ProgressResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/progress-update').then(m => m.ProgressUpdate),
    resolve: {
      progress: ProgressResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/progress-update').then(m => m.ProgressUpdate),
    resolve: {
      progress: ProgressResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default progressRoute;
