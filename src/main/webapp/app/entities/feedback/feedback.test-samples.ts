import { IFeedback, NewFeedback } from './feedback.model';

export const sampleWithRequiredData: IFeedback = {
  id: 16297,
  feedback: 'solder',
};

export const sampleWithPartialData: IFeedback = {
  id: 26360,
  feedback: 'without though eyebrow',
};

export const sampleWithFullData: IFeedback = {
  id: 18312,
  feedback: 'freight given bare',
  explanation: 'switch headline',
};

export const sampleWithNewData: NewFeedback = {
  feedback: 'faithfully',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
