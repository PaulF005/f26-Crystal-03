import { ILegalContent, NewLegalContent } from './legal-content.model';

export const sampleWithRequiredData: ILegalContent = {
  id: 13672,
  name: 'phooey who',
};

export const sampleWithPartialData: ILegalContent = {
  id: 2846,
  name: 'little victorious after',
};

export const sampleWithFullData: ILegalContent = {
  id: 15178,
  name: 'amidst violently',
};

export const sampleWithNewData: NewLegalContent = {
  name: 'made-up woot',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
