import { ScenarioResolution } from 'app/entities/enumerations/scenario-resolution.model';
import { IFeedback } from 'app/entities/feedback/feedback.model';
import { IQuestion } from 'app/entities/question/question.model';
import { IStage } from 'app/entities/stage/stage.model';

export interface IAnswer {
  id: number;
  text?: string | null;
  outcomeText?: string | null;
  correct?: boolean | null;
  terminalResolution?: keyof typeof ScenarioResolution | null;
  nextStage?: IStage | null;
  feedback?: IFeedback | null;
  question?: IQuestion | null;
}

export type NewAnswer = Omit<IAnswer, 'id'> & { id: null };
