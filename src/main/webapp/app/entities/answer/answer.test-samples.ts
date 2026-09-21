import { IAnswer, NewAnswer } from './answer.model';

export const sampleWithRequiredData: IAnswer = {
  id: 25973,
  text: 'whoa anenst refine',
};

export const sampleWithPartialData: IAnswer = {
  id: 28405,
  text: 'nucleotidase yippee bah',
  correct: false,
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
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
