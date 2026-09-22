import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import AnswerResolve from './route/answer-routing-resolve.service';

const answerRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/answer').then(m => m.Answer),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/answer-detail').then(m => m.AnswerDetail),
    resolve: {
      answer: AnswerResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/answer-update').then(m => m.AnswerUpdate),
    resolve: {
      answer: AnswerResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/answer-update').then(m => m.AnswerUpdate),
    resolve: {
      answer: AnswerResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default answerRoute;
