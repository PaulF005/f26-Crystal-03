export interface ILegalContent {
  id: number;
  name?: string | null;
}

export type NewLegalContent = Omit<ILegalContent, 'id'> & { id: null };
