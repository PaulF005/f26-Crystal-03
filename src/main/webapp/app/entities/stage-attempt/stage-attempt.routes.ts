import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import StageAttemptResolve from './route/stage-attempt-routing-resolve.service';

const stageAttemptRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/stage-attempt').then(m => m.StageAttempt),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/stage-attempt-detail').then(m => m.StageAttemptDetail),
    resolve: {
      stageAttempt: StageAttemptResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/stage-attempt-update').then(m => m.StageAttemptUpdate),
    resolve: {
      stageAttempt: StageAttemptResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/stage-attempt-update').then(m => m.StageAttemptUpdate),
    resolve: {
      stageAttempt: StageAttemptResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default stageAttemptRoute;
