import { IUserDetail, NewUserDetail } from './user-detail.model';

export const sampleWithRequiredData: IUserDetail = {
  id: 8519,
  username: 'absolve upbeat',
  email: 'Amira.McClure@gmail.com',
};

export const sampleWithPartialData: IUserDetail = {
  id: 28403,
  username: 'tooth silently powerfully',
  email: 'Coralie.Schneider@hotmail.com',
};

export const sampleWithFullData: IUserDetail = {
  id: 30711,
  username: 'minus graffiti',
  email: 'Dandre_Bernhard9@hotmail.com',
};

export const sampleWithNewData: NewUserDetail = {
  username: 'plus',
  email: 'Braeden.Witting67@yahoo.com',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
