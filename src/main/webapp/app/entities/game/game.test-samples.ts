import { IGame, NewGame } from './game.model';

export const sampleWithRequiredData: IGame = {
  id: 8692,
  name: 'entice wetly',
};

export const sampleWithPartialData: IGame = {
  id: 19528,
  name: 'without plus handover',
};

export const sampleWithFullData: IGame = {
  id: 6329,
  name: 'ah ugh',
};

export const sampleWithNewData: NewGame = {
  name: 'rationalise ridge righteously',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
