import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '0df61b76-e8ab-4a24-8fbc-c50f59b41ff9',
};

export const sampleWithPartialData: IAuthority = {
  name: 'e3d74e3f-1714-4f10-9f79-68552084c185',
};

export const sampleWithFullData: IAuthority = {
  name: 'e1cc2dd7-ef83-48db-a051-a9a0d62dad9a',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
