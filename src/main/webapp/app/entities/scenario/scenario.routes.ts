import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ScenarioResolve from './route/scenario-routing-resolve.service';

const scenarioRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/scenario').then(m => m.Scenario),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/scenario-detail').then(m => m.ScenarioDetail),
    resolve: {
      scenario: ScenarioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/scenario-update').then(m => m.ScenarioUpdate),
    resolve: {
      scenario: ScenarioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/scenario-update').then(m => m.ScenarioUpdate),
    resolve: {
      scenario: ScenarioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default scenarioRoute;
