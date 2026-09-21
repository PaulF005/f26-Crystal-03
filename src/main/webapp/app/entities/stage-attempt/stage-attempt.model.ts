import dayjs from 'dayjs/esm';

import { IAnswer } from 'app/entities/answer/answer.model';
import { IGameSession } from 'app/entities/game-session/game-session.model';
import { IStage } from 'app/entities/stage/stage.model';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';

export interface IStageAttempt {
  id: number;
  answeredAt?: dayjs.Dayjs | null;
  correct?: boolean | null;
  user?: IUserProfile | null;
  stage?: IStage | null;
  selectedAnswer?: IAnswer | null;
  gameSession?: IGameSession | null;
}

export type NewStageAttempt = Omit<IStageAttempt, 'id'> & { id: null };
