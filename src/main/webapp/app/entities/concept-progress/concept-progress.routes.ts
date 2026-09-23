import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import ConceptProgressResolve from './route/concept-progress-routing-resolve.service';

const conceptProgressRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/concept-progress').then(m => m.ConceptProgress),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/concept-progress-detail').then(m => m.ConceptProgressDetail),
    resolve: {
      conceptProgress: ConceptProgressResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/concept-progress-update').then(m => m.ConceptProgressUpdate),
    resolve: {
      conceptProgress: ConceptProgressResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/concept-progress-update').then(m => m.ConceptProgressUpdate),
    resolve: {
      conceptProgress: ConceptProgressResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default conceptProgressRoute;
