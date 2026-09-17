import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'Authorities' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'user-detail',
    data: { pageTitle: 'UserDetails' },
    loadChildren: () => import('./user-detail/user-detail.routes'),
  },
  {
    path: 'progress',
    data: { pageTitle: 'Progresses' },
    loadChildren: () => import('./progress/progress.routes'),
  },
  {
    path: 'topic',
    data: { pageTitle: 'Topics' },
    loadChildren: () => import('./topic/topic.routes'),
  },
  {
    path: 'concept',
    data: { pageTitle: 'Concepts' },
    loadChildren: () => import('./concept/concept.routes'),
  },
  {
    path: 'question',
    data: { pageTitle: 'Questions' },
    loadChildren: () => import('./question/question.routes'),
  },
  {
    path: 'answer',
    data: { pageTitle: 'Answers' },
    loadChildren: () => import('./answer/answer.routes'),
  },
  {
    path: 'feedback',
    data: { pageTitle: 'Feedbacks' },
    loadChildren: () => import('./feedback/feedback.routes'),
  },
  {
    path: 'game',
    data: { pageTitle: 'Games' },
    loadChildren: () => import('./game/game.routes'),
  },
  {
    path: 'scenario',
    data: { pageTitle: 'Scenarios' },
    loadChildren: () => import('./scenario/scenario.routes'),
  },
  {
    path: 'stage',
    data: { pageTitle: 'Stages' },
    loadChildren: () => import('./stage/stage.routes'),
  },
  {
    path: 'legal-content',
    data: { pageTitle: 'LegalContents' },
    loadChildren: () => import('./legal-content/legal-content.routes'),
  },
  {
    path: 'source',
    data: { pageTitle: 'Sources' },
    loadChildren: () => import('./source/source.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'UserManagements' },
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
