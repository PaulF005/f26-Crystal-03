import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import TopicProgressResolve from './route/topic-progress-routing-resolve.service';

const topicProgressRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/topic-progress').then(m => m.TopicProgress),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/topic-progress-detail').then(m => m.TopicProgressDetail),
    resolve: {
      topicProgress: TopicProgressResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/topic-progress-update').then(m => m.TopicProgressUpdate),
    resolve: {
      topicProgress: TopicProgressResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/topic-progress-update').then(m => m.TopicProgressUpdate),
    resolve: {
      topicProgress: TopicProgressResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default topicProgressRoute;
