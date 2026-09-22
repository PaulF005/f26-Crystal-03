import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import GameProgressResolve from './route/game-progress-routing-resolve.service';

const gameProgressRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/game-progress').then(m => m.GameProgress),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/game-progress-detail').then(m => m.GameProgressDetail),
    resolve: {
      gameProgress: GameProgressResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/game-progress-update').then(m => m.GameProgressUpdate),
    resolve: {
      gameProgress: GameProgressResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/game-progress-update').then(m => m.GameProgressUpdate),
    resolve: {
      gameProgress: GameProgressResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default gameProgressRoute;
