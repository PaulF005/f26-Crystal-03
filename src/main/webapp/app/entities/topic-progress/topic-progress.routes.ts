import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import TopicProgressResolve from './route/topic-progress-routing-resolve.service';

const topicProgressRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/topic-progress').then(m => m.TopicProgress),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/topic-progress-detail').then(m => m.TopicProgressDetail),
    resolve: {
      topicProgress: TopicProgressResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/topic-progress-update').then(m => m.TopicProgressUpdate),
    resolve: {
      topicProgress: TopicProgressResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/topic-progress-update').then(m => m.TopicProgressUpdate),
    resolve: {
      topicProgress: TopicProgressResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default topicProgressRoute;
