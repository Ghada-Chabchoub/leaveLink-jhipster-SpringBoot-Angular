import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '235342f2-eade-4c5e-b13b-b86160400f00',
};

export const sampleWithPartialData: IAuthority = {
  name: '356c80d5-c732-479c-a7fd-840c594aed60',
};

export const sampleWithFullData: IAuthority = {
  name: 'caa27162-dddd-4f8f-b50e-fe9f44952e2e',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
