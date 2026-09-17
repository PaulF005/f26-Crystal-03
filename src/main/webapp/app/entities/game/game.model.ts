export interface IGame {
  id: number;
  name?: string | null;
}

export type NewGame = Omit<IGame, 'id'> & { id: null };
