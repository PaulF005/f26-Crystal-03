import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import SourceResolve from './route/source-routing-resolve.service';

const sourceRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/source').then(m => m.Source),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/source-detail').then(m => m.SourceDetail),
    resolve: {
      source: SourceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/source-update').then(m => m.SourceUpdate),
    resolve: {
      source: SourceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/source-update').then(m => m.SourceUpdate),
    resolve: {
      source: SourceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default sourceRoute;
