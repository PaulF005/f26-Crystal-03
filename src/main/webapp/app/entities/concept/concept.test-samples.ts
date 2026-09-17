import { IConcept, NewConcept } from './concept.model';

export const sampleWithRequiredData: IConcept = {
  id: 26674,
  name: 'duh boohoo',
};

export const sampleWithPartialData: IConcept = {
  id: 12568,
  name: 'wicked boohoo',
  explanation: 'remarkable',
};

export const sampleWithFullData: IConcept = {
  id: 29549,
  name: 'meh circa',
  explanation: 'young',
};

export const sampleWithNewData: NewConcept = {
  name: 'mockingly doubter whose',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
