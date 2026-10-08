import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import QuestionResolve from './route/question-routing-resolve.service';

const questionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/question').then(m => m.Question),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/question-detail').then(m => m.QuestionDetail),
    resolve: {
      question: QuestionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/question-update').then(m => m.QuestionUpdate),
    resolve: {
      question: QuestionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/question-update').then(m => m.QuestionUpdate),
    resolve: {
      question: QuestionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default questionRoute;
