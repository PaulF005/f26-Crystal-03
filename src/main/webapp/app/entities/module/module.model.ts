export interface IModule {
  id: number;
  name?: string | null;
}

export type NewModule = Omit<IModule, 'id'> & { id: null };
