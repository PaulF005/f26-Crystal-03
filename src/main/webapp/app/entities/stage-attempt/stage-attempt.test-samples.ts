import dayjs from 'dayjs/esm';

import { IStageAttempt, NewStageAttempt } from './stage-attempt.model';

export const sampleWithRequiredData: IStageAttempt = {
  id: 9866,
  answeredAt: dayjs('2026-09-20T20:31'),
  correct: false,
};

export const sampleWithPartialData: IStageAttempt = {
  id: 24052,
  answeredAt: dayjs('2026-09-20T05:35'),
  correct: true,
};

export const sampleWithFullData: IStageAttempt = {
  id: 25596,
  answeredAt: dayjs('2026-09-20T19:15'),
  correct: false,
};

export const sampleWithNewData: NewStageAttempt = {
  answeredAt: dayjs('2026-09-20T16:30'),
  correct: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
