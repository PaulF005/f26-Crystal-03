import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import TopicResolve from './route/topic-routing-resolve.service';

const topicRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/topic').then(m => m.Topic),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/topic-detail').then(m => m.TopicDetail),
    resolve: {
      topic: TopicResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/topic-update').then(m => m.TopicUpdate),
    resolve: {
      topic: TopicResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/topic-update').then(m => m.TopicUpdate),
    resolve: {
      topic: TopicResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default topicRoute;
