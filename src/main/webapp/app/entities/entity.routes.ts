import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'user-management',
    title: 'UserManagements',
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  {
    path: 'authority',
    title: 'Authorities',
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'user-profile',
    title: 'UserProfiles',
    loadChildren: () => import('./user-profile/user-profile.routes'),
  },
  {
    path: 'topic',
    title: 'Topics',
    loadChildren: () => import('./topic/topic.routes'),
  },
  {
    path: 'concept',
    title: 'Concepts',
    loadChildren: () => import('./concept/concept.routes'),
  },
  {
    path: 'question',
    title: 'Questions',
    loadChildren: () => import('./question/question.routes'),
  },
  {
    path: 'answer',
    title: 'Answers',
    loadChildren: () => import('./answer/answer.routes'),
  },
  {
    path: 'feedback',
    title: 'Feedbacks',
    loadChildren: () => import('./feedback/feedback.routes'),
  },
  {
    path: 'game',
    title: 'Games',
    loadChildren: () => import('./game/game.routes'),
  },
  {
    path: 'scenario',
    title: 'Scenarios',
    loadChildren: () => import('./scenario/scenario.routes'),
  },
  {
    path: 'stage',
    title: 'Stages',
    loadChildren: () => import('./stage/stage.routes'),
  },
  {
    path: 'legal-content',
    title: 'LegalContents',
    loadChildren: () => import('./legal-content/legal-content.routes'),
  },
  {
    path: 'source',
    title: 'Sources',
    loadChildren: () => import('./source/source.routes'),
  },
  {
    path: 'topic-progress',
    title: 'TopicProgresses',
    loadChildren: () => import('./topic-progress/topic-progress.routes'),
  },
  {
    path: 'game-progress',
    title: 'GameProgresses',
    loadChildren: () => import('./game-progress/game-progress.routes'),
  },
  {
    path: 'game-session',
    title: 'GameSessions',
    loadChildren: () => import('./game-session/game-session.routes'),
  },
  {
    path: 'stage-attempt',
    title: 'StageAttempts',
    loadChildren: () => import('./stage-attempt/stage-attempt.routes'),
  },
  {
    path: 'concept-progress',
    title: 'ConceptProgresses',
    loadChildren: () => import('./concept-progress/concept-progress.routes'),
  },
  {
    path: 'user-detail',
    title: 'UserDetails',
    loadChildren: () => import('./user-detail/user-detail.routes'),
  },
  {
    path: 'progress',
    title: 'Progresses',
    loadChildren: () => import('./progress/progress.routes'),
  },
  // jhipster-needle-add-entity-route - JHipster will add entity modules routes here
];

export default routes;
