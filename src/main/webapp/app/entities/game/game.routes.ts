import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import GameResolve from './route/game-routing-resolve.service';

const gameRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/game').then(m => m.Game),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/game-detail').then(m => m.GameDetail),
    resolve: {
      game: GameResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/game-update').then(m => m.GameUpdate),
    resolve: {
      game: GameResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/game-update').then(m => m.GameUpdate),
    resolve: {
      game: GameResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default gameRoute;
