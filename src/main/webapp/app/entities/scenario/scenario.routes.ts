import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import ScenarioResolve from './route/scenario-routing-resolve.service';

const scenarioRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/scenario').then(m => m.Scenario),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/scenario-detail').then(m => m.ScenarioDetail),
    resolve: {
      scenario: ScenarioResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/scenario-update').then(m => m.ScenarioUpdate),
    resolve: {
      scenario: ScenarioResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/scenario-update').then(m => m.ScenarioUpdate),
    resolve: {
      scenario: ScenarioResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default scenarioRoute;
