import { ILegalContent } from 'app/entities/legal-content/legal-content.model';
import { IModule } from 'app/entities/module/module.model';

export interface ITopic {
  id: number;
  name?: string | null;
  explanation?: string | null;
  legalContent?: ILegalContent | null;
  module?: IModule | null;
}

export type NewTopic = Omit<ITopic, 'id'> & { id: null };
