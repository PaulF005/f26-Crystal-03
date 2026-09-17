import { IStage, NewStage } from './stage.model';

export const sampleWithRequiredData: IStage = {
  id: 24685,
};

export const sampleWithPartialData: IStage = {
  id: 2451,
};

export const sampleWithFullData: IStage = {
  id: 9717,
};

export const sampleWithNewData: NewStage = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
