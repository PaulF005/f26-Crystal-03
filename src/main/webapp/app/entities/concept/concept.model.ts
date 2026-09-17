import { ILegalContent } from 'app/entities/legal-content/legal-content.model';
import { ITopic } from 'app/entities/topic/topic.model';

export interface IConcept {
  id: number;
  name?: string | null;
  explanation?: string | null;
  legalContent?: ILegalContent | null;
  topic?: ITopic | null;
}

export type NewConcept = Omit<IConcept, 'id'> & { id: null };
