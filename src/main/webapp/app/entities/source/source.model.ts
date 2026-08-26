import { ILegalContent } from 'app/entities/legal-content/legal-content.model';

export interface ISource {
  id: number;
  name?: string | null;
  url?: string | null;
  date?: string | null;
  data?: string | null;
  legalContent?: ILegalContent | null;
}

export type NewSource = Omit<ISource, 'id'> & { id: null };
