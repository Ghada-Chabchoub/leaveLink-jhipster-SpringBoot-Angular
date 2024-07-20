import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 2641,
  login: 'HF',
};

export const sampleWithPartialData: IUser = {
  id: 11335,
  login: 'LLk',
};

export const sampleWithFullData: IUser = {
  id: 31567,
  login: 'n@P\\VylsXyg\\aL',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
