import { IGame } from 'app/entities/game/game.model';
import { IStage } from 'app/entities/stage/stage.model';
import { ITopic } from 'app/entities/topic/topic.model';

export interface IScenario {
  id: number;
  name?: string | null;
  startingStage?: IStage | null;
  topic?: ITopic | null;
  game?: IGame | null;
}

export type NewScenario = Omit<IScenario, 'id'> & { id: null };
