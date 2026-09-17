import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import StageResolve from './route/stage-routing-resolve.service';

const stageRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/stage').then(m => m.Stage),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/stage-detail').then(m => m.StageDetail),
    resolve: {
      stage: StageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/stage-update').then(m => m.StageUpdate),
    resolve: {
      stage: StageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/stage-update').then(m => m.StageUpdate),
    resolve: {
      stage: StageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default stageRoute;
