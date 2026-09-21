import dayjs from 'dayjs/esm';

import { IConceptProgress, NewConceptProgress } from './concept-progress.model';

export const sampleWithRequiredData: IConceptProgress = {
  id: 25630,
  competency: 0.75,
  improvement: 4330.5,
  evidenceCount: 8353,
  lastPracticedAt: dayjs('2026-09-20T09:40'),
};

export const sampleWithPartialData: IConceptProgress = {
  id: 15704,
  competency: 0.02,
  improvement: 29755.34,
  evidenceCount: 3958,
  lastPracticedAt: dayjs('2026-09-20T10:25'),
};

export const sampleWithFullData: IConceptProgress = {
  id: 32305,
  competency: 0.05,
  improvement: 4169.12,
  evidenceCount: 23310,
  lastPracticedAt: dayjs('2026-09-20T09:52'),
};

export const sampleWithNewData: NewConceptProgress = {
  competency: 0.17,
  improvement: 19919.96,
  evidenceCount: 3797,
  lastPracticedAt: dayjs('2026-09-20T00:44'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
