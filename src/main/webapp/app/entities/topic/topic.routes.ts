import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import TopicResolve from './route/topic-routing-resolve.service';

const topicRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/topic').then(m => m.Topic),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/topic-detail').then(m => m.TopicDetail),
    resolve: {
      topic: TopicResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/topic-update').then(m => m.TopicUpdate),
    resolve: {
      topic: TopicResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/topic-update').then(m => m.TopicUpdate),
    resolve: {
      topic: TopicResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default topicRoute;
