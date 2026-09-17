export interface IFeedback {
  id: number;
  feedback?: string | null;
  explanation?: string | null;
}

export type NewFeedback = Omit<IFeedback, 'id'> & { id: null };
