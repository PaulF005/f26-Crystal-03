import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import LegalContentResolve from './route/legal-content-routing-resolve.service';

const legalContentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/legal-content').then(m => m.LegalContent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/legal-content-detail').then(m => m.LegalContentDetail),
    resolve: {
      legalContent: LegalContentResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/legal-content-update').then(m => m.LegalContentUpdate),
    resolve: {
      legalContent: LegalContentResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/legal-content-update').then(m => m.LegalContentUpdate),
    resolve: {
      legalContent: LegalContentResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default legalContentRoute;
