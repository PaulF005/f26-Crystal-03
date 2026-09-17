import { IQuestion } from 'app/entities/question/question.model';
import { IScenario } from 'app/entities/scenario/scenario.model';

export interface IStage {
  id: number;
  question?: IQuestion | null;
  scenario?: IScenario | null;
}

export type NewStage = Omit<IStage, 'id'> & { id: null };
