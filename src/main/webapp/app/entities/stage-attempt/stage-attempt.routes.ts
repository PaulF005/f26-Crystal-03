import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import StageAttemptResolve from './route/stage-attempt-routing-resolve.service';

const stageAttemptRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/stage-attempt').then(m => m.StageAttempt),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/stage-attempt-detail').then(m => m.StageAttemptDetail),
    resolve: {
      stageAttempt: StageAttemptResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/stage-attempt-update').then(m => m.StageAttemptUpdate),
    resolve: {
      stageAttempt: StageAttemptResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/stage-attempt-update').then(m => m.StageAttemptUpdate),
    resolve: {
      stageAttempt: StageAttemptResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default stageAttemptRoute;
