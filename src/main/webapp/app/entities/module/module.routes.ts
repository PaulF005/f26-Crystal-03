import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ModuleResolve from './route/module-routing-resolve.service';

const moduleRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/module').then(m => m.Module),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/module-detail').then(m => m.ModuleDetail),
    resolve: {
      module: ModuleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/module-update').then(m => m.ModuleUpdate),
    resolve: {
      module: ModuleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/module-update').then(m => m.ModuleUpdate),
    resolve: {
      module: ModuleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default moduleRoute;
