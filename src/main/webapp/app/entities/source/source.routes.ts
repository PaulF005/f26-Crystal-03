import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import SourceResolve from './route/source-routing-resolve.service';

const sourceRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/source').then(m => m.Source),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/source-detail').then(m => m.SourceDetail),
    resolve: {
      source: SourceResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/source-update').then(m => m.SourceUpdate),
    resolve: {
      source: SourceResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/source-update').then(m => m.SourceUpdate),
    resolve: {
      source: SourceResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default sourceRoute;
