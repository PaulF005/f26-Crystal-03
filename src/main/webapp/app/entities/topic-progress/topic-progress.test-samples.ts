import dayjs from 'dayjs/esm';

import { ITopicProgress, NewTopicProgress } from './topic-progress.model';

export const sampleWithRequiredData: ITopicProgress = {
  id: 2948,
  competency: 0.75,
  improvement: 25099.78,
  evidenceCount: 13109,
  lastPracticedAt: dayjs('2026-09-20T01:44'),
};

export const sampleWithPartialData: ITopicProgress = {
  id: 8262,
  competency: 0.43,
  improvement: 2558.43,
  evidenceCount: 31887,
  lastPracticedAt: dayjs('2026-09-20T17:12'),
};

export const sampleWithFullData: ITopicProgress = {
  id: 16864,
  competency: 0.72,
  improvement: 27193.17,
  evidenceCount: 22665,
  lastPracticedAt: dayjs('2026-09-20T13:39'),
};

export const sampleWithNewData: NewTopicProgress = {
  competency: 0.37,
  improvement: 6983.4,
  evidenceCount: 22113,
  lastPracticedAt: dayjs('2026-09-20T06:28'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
