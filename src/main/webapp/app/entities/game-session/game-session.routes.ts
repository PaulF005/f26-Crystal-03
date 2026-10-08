import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import GameSessionResolve from './route/game-session-routing-resolve.service';

const gameSessionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/game-session').then(m => m.GameSession),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/game-session-detail').then(m => m.GameSessionDetail),
    resolve: {
      gameSession: GameSessionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/game-session-update').then(m => m.GameSessionUpdate),
    resolve: {
      gameSession: GameSessionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/game-session-update').then(m => m.GameSessionUpdate),
    resolve: {
      gameSession: GameSessionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default gameSessionRoute;
