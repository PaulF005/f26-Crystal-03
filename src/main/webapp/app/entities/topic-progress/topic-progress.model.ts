import dayjs from 'dayjs/esm';

import { ITopic } from 'app/entities/topic/topic.model';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';

export interface ITopicProgress {
  id: number;
  competency?: number | null;
  improvement?: number | null;
  evidenceCount?: number | null;
  lastPracticedAt?: dayjs.Dayjs | null;
  topic?: ITopic | null;
  user?: IUserProfile | null;
  userProfile?: IUserProfile | null;
}

export type NewTopicProgress = Omit<ITopicProgress, 'id'> & { id: null };
