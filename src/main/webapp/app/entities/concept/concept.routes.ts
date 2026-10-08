import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ConceptResolve from './route/concept-routing-resolve.service';

const conceptRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/concept').then(m => m.Concept),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/concept-detail').then(m => m.ConceptDetail),
    resolve: {
      concept: ConceptResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/concept-update').then(m => m.ConceptUpdate),
    resolve: {
      concept: ConceptResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/concept-update').then(m => m.ConceptUpdate),
    resolve: {
      concept: ConceptResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default conceptRoute;
