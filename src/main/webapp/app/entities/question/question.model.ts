import { IConcept } from 'app/entities/concept/concept.model';

export interface IQuestion {
  id: number;
  question?: string | null;
  concept?: IConcept | null;
}

export type NewQuestion = Omit<IQuestion, 'id'> & { id: null };
