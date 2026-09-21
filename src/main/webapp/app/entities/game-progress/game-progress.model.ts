import dayjs from 'dayjs/esm';

import { IGame } from 'app/entities/game/game.model';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';

export interface IGameProgress {
  id: number;
  sessionsPlayed?: number | null;
  performance?: number | null;
  evidenceCount?: number | null;
  lastPlayedAt?: dayjs.Dayjs | null;
  game?: IGame | null;
  user?: IUserProfile | null;
  userProfile?: IUserProfile | null;
}

export type NewGameProgress = Omit<IGameProgress, 'id'> & { id: null };
