import { IUser } from 'app/entities/user/user.model';

export interface IUserDetail {
  id: number;
  username?: string | null;
  email?: string | null;
  dataUser?: Pick<IUser, 'id'> | null;
}

export type NewUserDetail = Omit<IUserDetail, 'id'> & { id: null };
