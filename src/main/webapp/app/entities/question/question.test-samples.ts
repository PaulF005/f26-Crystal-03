import { IQuestion, NewQuestion } from './question.model';

export const sampleWithRequiredData: IQuestion = {
  id: 1630,
  question: 'bob gosh adventurously',
};

export const sampleWithPartialData: IQuestion = {
  id: 26820,
  question: 'amid',
};

export const sampleWithFullData: IQuestion = {
  id: 28102,
  question: 'sweet',
};

export const sampleWithNewData: NewQuestion = {
  question: 'successfully inasmuch until',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
