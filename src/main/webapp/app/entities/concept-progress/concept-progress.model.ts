import dayjs from 'dayjs/esm';

import { IConcept } from 'app/entities/concept/concept.model';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';

export interface IConceptProgress {
  id: number;
  competency?: number | null;
  improvement?: number | null;
  evidenceCount?: number | null;
  lastPracticedAt?: dayjs.Dayjs | null;
  maxQuestions?: number | null;
  userProfile?: IUserProfile | null;
  concept?: IConcept | null;
}

export type NewConceptProgress = Omit<IConceptProgress, 'id'> & { id: null };
