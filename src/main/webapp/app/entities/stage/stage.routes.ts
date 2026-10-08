import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import StageResolve from './route/stage-routing-resolve.service';

const stageRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/stage').then(m => m.Stage),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/stage-detail').then(m => m.StageDetail),
    resolve: {
      stage: StageResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/stage-update').then(m => m.StageUpdate),
    resolve: {
      stage: StageResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/stage-update').then(m => m.StageUpdate),
    resolve: {
      stage: StageResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default stageRoute;
