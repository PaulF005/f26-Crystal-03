import { ISource, NewSource } from './source.model';

export const sampleWithRequiredData: ISource = {
  id: 7582,
  name: 'meaningfully smoke finally',
  data: 'over boo',
};

export const sampleWithPartialData: ISource = {
  id: 6163,
  name: 'correctly jaggedly phew',
  date: 'duh',
  data: 'galvanize broadside',
};

export const sampleWithFullData: ISource = {
  id: 10639,
  name: 'readily',
  url: 'https://possible-trash.org/',
  date: 'ah',
  data: 'fork',
};

export const sampleWithNewData: NewSource = {
  name: 'between acceptable',
  data: 'wrathful hospitalization often',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
