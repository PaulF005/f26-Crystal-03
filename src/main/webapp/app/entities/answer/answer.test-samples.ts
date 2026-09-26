import { IAnswer, NewAnswer } from './answer.model';

export const sampleWithRequiredData: IAnswer = {
  id: 25973,
  text: 'whoa anenst refine',
  correct: false,
};

export const sampleWithPartialData: IAnswer = {
  id: 26021,
  text: 'acquire boohoo table',
  correct: false,
  terminalResolution: 'INCONCLUSIVE',
};

export const sampleWithFullData: IAnswer = {
  id: 29585,
  text: 'yum',
  outcomeText: 'devastation',
  correct: true,
  terminalResolution: 'POSITIVE',
};

export const sampleWithNewData: NewAnswer = {
  text: 'ugh',
  correct: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
