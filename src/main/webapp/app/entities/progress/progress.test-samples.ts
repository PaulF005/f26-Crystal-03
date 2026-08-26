import { IProgress, NewProgress } from './progress.model';

export const sampleWithRequiredData: IProgress = {
  id: 1908,
  moduleCompletion: 2772.37,
};

export const sampleWithPartialData: IProgress = {
  id: 9808,
  moduleCompletion: 22041.03,
};

export const sampleWithFullData: IProgress = {
  id: 26118,
  moduleCompletion: 25691.14,
};

export const sampleWithNewData: NewProgress = {
  moduleCompletion: 20458,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
