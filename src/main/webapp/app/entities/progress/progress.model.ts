import { IModule } from 'app/entities/module/module.model';
import { IUserDetail } from 'app/entities/user-detail/user-detail.model';

export interface IProgress {
  id: number;
  moduleCompletion?: number | null;
  module?: IModule | null;
  user?: IUserDetail | null;
}

export type NewProgress = Omit<IProgress, 'id'> & { id: null };
