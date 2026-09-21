import dayjs from 'dayjs/esm';

import { IGameSession, NewGameSession } from './game-session.model';

export const sampleWithRequiredData: IGameSession = {
  id: 21816,
  startedAt: dayjs('2026-09-20T07:32'),
  completedAt: dayjs('2026-09-20T17:05'),
};

export const sampleWithPartialData: IGameSession = {
  id: 6269,
  startedAt: dayjs('2026-09-20T15:14'),
  completedAt: dayjs('2026-09-20T01:14'),
};

export const sampleWithFullData: IGameSession = {
  id: 5267,
  startedAt: dayjs('2026-09-20T19:24'),
  completedAt: dayjs('2026-09-20T13:25'),
};

export const sampleWithNewData: NewGameSession = {
  startedAt: dayjs('2026-09-20T07:49'),
  completedAt: dayjs('2026-09-20T15:11'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
