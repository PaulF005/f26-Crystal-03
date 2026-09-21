import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ConceptProgressResolve from './route/concept-progress-routing-resolve.service';

const conceptProgressRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/concept-progress').then(m => m.ConceptProgress),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/concept-progress-detail').then(m => m.ConceptProgressDetail),
    resolve: {
      conceptProgress: ConceptProgressResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/concept-progress-update').then(m => m.ConceptProgressUpdate),
    resolve: {
      conceptProgress: ConceptProgressResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/concept-progress-update').then(m => m.ConceptProgressUpdate),
    resolve: {
      conceptProgress: ConceptProgressResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default conceptProgressRoute;
