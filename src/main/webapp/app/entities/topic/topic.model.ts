export interface ITopic {
  id: number;
  name?: string | null;
}

export type NewTopic = Omit<ITopic, 'id'> & { id: null };
