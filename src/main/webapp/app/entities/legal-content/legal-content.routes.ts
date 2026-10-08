import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import LegalContentResolve from './route/legal-content-routing-resolve.service';

const legalContentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/legal-content').then(m => m.LegalContent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/legal-content-detail').then(m => m.LegalContentDetail),
    resolve: {
      legalContent: LegalContentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/legal-content-update').then(m => m.LegalContentUpdate),
    resolve: {
      legalContent: LegalContentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/legal-content-update').then(m => m.LegalContentUpdate),
    resolve: {
      legalContent: LegalContentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default legalContentRoute;
