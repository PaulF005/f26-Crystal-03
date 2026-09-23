import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import QuestionResolve from './route/question-routing-resolve.service';

const questionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/question').then(m => m.Question),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/question-detail').then(m => m.QuestionDetail),
    resolve: {
      question: QuestionResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/question-update').then(m => m.QuestionUpdate),
    resolve: {
      question: QuestionResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/question-update').then(m => m.QuestionUpdate),
    resolve: {
      question: QuestionResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default questionRoute;
