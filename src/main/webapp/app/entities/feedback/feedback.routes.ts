import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import FeedbackResolve from './route/feedback-routing-resolve.service';

const feedbackRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/feedback').then(m => m.Feedback),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/feedback-detail').then(m => m.FeedbackDetail),
    resolve: {
      feedback: FeedbackResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/feedback-update').then(m => m.FeedbackUpdate),
    resolve: {
      feedback: FeedbackResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/feedback-update').then(m => m.FeedbackUpdate),
    resolve: {
      feedback: FeedbackResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default feedbackRoute;
