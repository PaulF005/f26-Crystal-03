import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { userRouteAccessService } from 'app/core/auth';

import ModuleResolve from './route/module-routing-resolve.service';

const moduleRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/module').then(m => m.Module),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/module-detail').then(m => m.ModuleDetail),
    resolve: {
      module: ModuleResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/module-update').then(m => m.ModuleUpdate),
    resolve: {
      module: ModuleResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/module-update').then(m => m.ModuleUpdate),
    resolve: {
      module: ModuleResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default moduleRoute;
