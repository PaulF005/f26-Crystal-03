import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import ConceptResolve from './route/concept-routing-resolve.service';

const conceptRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/concept').then(m => m.Concept),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/concept-detail').then(m => m.ConceptDetail),
    resolve: {
      concept: ConceptResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/concept-update').then(m => m.ConceptUpdate),
    resolve: {
      concept: ConceptResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/concept-update').then(m => m.ConceptUpdate),
    resolve: {
      concept: ConceptResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default conceptRoute;
