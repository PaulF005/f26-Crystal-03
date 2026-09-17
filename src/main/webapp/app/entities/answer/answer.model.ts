import { IFeedback } from 'app/entities/feedback/feedback.model';
import { IQuestion } from 'app/entities/question/question.model';
import { IStage } from 'app/entities/stage/stage.model';

export interface IAnswer {
  id: number;
  answer?: string | null;
  correct?: boolean | null;
  nextStage?: IStage | null;
  feedback?: IFeedback | null;
  question?: IQuestion | null;
}

export type NewAnswer = Omit<IAnswer, 'id'> & { id: null };
