import { IModule, NewModule } from './module.model';

export const sampleWithRequiredData: IModule = {
  id: 9747,
  name: 'now clear-cut qua',
};

export const sampleWithPartialData: IModule = {
  id: 20398,
  name: 'woot',
};

export const sampleWithFullData: IModule = {
  id: 1292,
  name: 'remark',
};

export const sampleWithNewData: NewModule = {
  name: 'meh frightfully',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
