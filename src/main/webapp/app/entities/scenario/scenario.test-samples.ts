import { IScenario, NewScenario } from './scenario.model';

export const sampleWithRequiredData: IScenario = {
  id: 32123,
  name: 'after if uh-huh',
};

export const sampleWithPartialData: IScenario = {
  id: 28487,
  name: 'drat',
};

export const sampleWithFullData: IScenario = {
  id: 1550,
  name: 'pro boohoo',
};

export const sampleWithNewData: NewScenario = {
  name: 'unusual archive confide',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
