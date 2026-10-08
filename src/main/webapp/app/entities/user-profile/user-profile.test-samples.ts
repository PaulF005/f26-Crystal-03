import { IUserProfile, NewUserProfile } from './user-profile.model';

export const sampleWithRequiredData: IUserProfile = {
  id: 4033,
  username: 'deselect ravioli fervently',
  email: 'Domenico.Medhurst64@yahoo.com',
};

export const sampleWithPartialData: IUserProfile = {
  id: 2358,
  username: 'intrepid',
  email: 'Claudia_Schimmel97@gmail.com',
};

export const sampleWithFullData: IUserProfile = {
  id: 9570,
  username: 'folklore glisten cuddly',
  email: 'Madelynn.Ortiz@hotmail.com',
};

export const sampleWithNewData: NewUserProfile = {
  username: 'ick astride awesome',
  email: 'Shea.Fay@gmail.com',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
