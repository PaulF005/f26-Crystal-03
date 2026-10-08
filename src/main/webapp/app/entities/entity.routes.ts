import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'Authorities' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'user-profile',
    data: { pageTitle: 'UserProfiles' },
    loadChildren: () => import('./user-profile/user-profile.routes'),
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
    path: 'topic-progress',
    data: { pageTitle: 'TopicProgresses' },
    loadChildren: () => import('./topic-progress/topic-progress.routes'),
  },
  {
    path: 'game-progress',
    data: { pageTitle: 'GameProgresses' },
    loadChildren: () => import('./game-progress/game-progress.routes'),
  },
  {
    path: 'game-session',
    data: { pageTitle: 'GameSessions' },
    loadChildren: () => import('./game-session/game-session.routes'),
  },
  {
    path: 'stage-attempt',
    data: { pageTitle: 'StageAttempts' },
    loadChildren: () => import('./stage-attempt/stage-attempt.routes'),
  },
  {
    path: 'concept-progress',
    data: { pageTitle: 'ConceptProgresses' },
    loadChildren: () => import('./concept-progress/concept-progress.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'UserManagements' },
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
