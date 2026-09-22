import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import GameSessionResolve from './route/game-session-routing-resolve.service';

const gameSessionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/game-session').then(m => m.GameSession),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/game-session-detail').then(m => m.GameSessionDetail),
    resolve: {
      gameSession: GameSessionResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/game-session-update').then(m => m.GameSessionUpdate),
    resolve: {
      gameSession: GameSessionResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/game-session-update').then(m => m.GameSessionUpdate),
    resolve: {
      gameSession: GameSessionResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default gameSessionRoute;
