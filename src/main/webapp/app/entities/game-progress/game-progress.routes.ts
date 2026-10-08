import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import GameProgressResolve from './route/game-progress-routing-resolve.service';

const gameProgressRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/game-progress').then(m => m.GameProgress),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/game-progress-detail').then(m => m.GameProgressDetail),
    resolve: {
      gameProgress: GameProgressResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/game-progress-update').then(m => m.GameProgressUpdate),
    resolve: {
      gameProgress: GameProgressResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/game-progress-update').then(m => m.GameProgressUpdate),
    resolve: {
      gameProgress: GameProgressResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default gameProgressRoute;
