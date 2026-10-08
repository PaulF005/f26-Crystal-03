import { Routes } from '@angular/router';

import { userRouteAccessService } from 'app/core/auth';
import { Authority } from 'app/shared/jhipster/constants';

import { errorRoute } from './layouts/error/error.route';

const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./home/home'),
    title: 'Welcome, Java Hipster!',
  },
  {
    path: '',
    loadComponent: () => import('./layouts/navbar/navbar'),
    outlet: 'navbar',
  },
  {
    path: 'admin',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
    loadChildren: () => import('./admin/admin.routes'),
  },
  {
    path: 'account',
    loadChildren: () => import('./account/account.route'),
  },
  {
    path: 'login',
    loadComponent: () => import('./login/login'),
    title: 'Sign in',
  },
  {
    path: 'settings',
    loadComponent: () => import('./settings/settings'),
    data: {
      authorities: [Authority.USER],
    },
    canActivate: [userRouteAccessService],
    title: 'Settings',
  },
  {
    path: '',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [userRouteAccessService],
    loadChildren: () => import('./entities/entity.routes'),
  },
  {
    path: 'games',
    loadComponent: () => import('./games/games'),
    title: 'Games',
  },
  {
    path: 'my-progress',
    loadComponent: () => import('./progress/progress'),
    title: 'Progress',
  },
  ...errorRoute,
];

export default routes;
