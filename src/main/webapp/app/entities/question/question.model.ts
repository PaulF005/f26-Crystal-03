import { ITopic } from 'app/entities/topic/topic.model';

export interface IQuestion {
  id: number;
  question?: string | null;
  topic?: ITopic | null;
}

export type NewQuestion = Omit<IQuestion, 'id'> & { id: null };
