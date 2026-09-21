import { IUser } from 'app/entities/user/user.model';

export interface IUserProfile {
  id: number;
  username?: string | null;
  email?: string | null;
  dataUser?: Pick<IUser, 'id'> | null;
}

export type NewUserProfile = Omit<IUserProfile, 'id'> & { id: null };
