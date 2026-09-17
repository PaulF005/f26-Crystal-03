import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import AnswerResolve from './route/answer-routing-resolve.service';

const answerRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/answer').then(m => m.Answer),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/answer-detail').then(m => m.AnswerDetail),
    resolve: {
      answer: AnswerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/answer-update').then(m => m.AnswerUpdate),
    resolve: {
      answer: AnswerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/answer-update').then(m => m.AnswerUpdate),
    resolve: {
      answer: AnswerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default answerRoute;
