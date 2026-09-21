import dayjs from 'dayjs/esm';

import { IGameProgress, NewGameProgress } from './game-progress.model';

export const sampleWithRequiredData: IGameProgress = {
  id: 8396,
  sessionsPlayed: 5544,
  performance: 0.27,
  evidenceCount: 26621,
  lastPlayedAt: dayjs('2026-09-20T01:01'),
};

export const sampleWithPartialData: IGameProgress = {
  id: 30808,
  sessionsPlayed: 7208,
  performance: 0.18,
  evidenceCount: 28771,
  lastPlayedAt: dayjs('2026-09-20T08:30'),
};

export const sampleWithFullData: IGameProgress = {
  id: 3621,
  sessionsPlayed: 6885,
  performance: 0.52,
  evidenceCount: 11464,
  lastPlayedAt: dayjs('2026-09-20T11:22'),
};

export const sampleWithNewData: NewGameProgress = {
  sessionsPlayed: 29737,
  performance: 0.87,
  evidenceCount: 11912,
  lastPlayedAt: dayjs('2026-09-19T18:25'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
