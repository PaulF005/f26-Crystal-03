import dayjs from 'dayjs/esm';

import { IGame } from 'app/entities/game/game.model';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';

export interface IGameSession {
  id: number;
  startedAt?: dayjs.Dayjs | null;
  completedAt?: dayjs.Dayjs | null;
  user?: IUserProfile | null;
  game?: IGame | null;
}

export type NewGameSession = Omit<IGameSession, 'id'> & { id: null };
