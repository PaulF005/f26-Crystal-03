import { ITopic, NewTopic } from './topic.model';

export const sampleWithRequiredData: ITopic = {
  id: 18714,
  name: 'swordfish',
};

export const sampleWithPartialData: ITopic = {
  id: 27054,
  name: 'questionably',
};

export const sampleWithFullData: ITopic = {
  id: 1471,
  name: 'brr gosh and',
  explanation: 'babushka',
};

export const sampleWithNewData: NewTopic = {
  name: 'once',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
