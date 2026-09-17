import { IAnswer, NewAnswer } from './answer.model';

export const sampleWithRequiredData: IAnswer = {
  id: 25973,
  answer: 'whoa anenst refine',
  correct: false,
};

export const sampleWithPartialData: IAnswer = {
  id: 23134,
  answer: 'why midst',
  correct: true,
};

export const sampleWithFullData: IAnswer = {
  id: 29585,
  answer: 'yum',
  correct: true,
};

export const sampleWithNewData: NewAnswer = {
  answer: 'ugh',
  correct: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
