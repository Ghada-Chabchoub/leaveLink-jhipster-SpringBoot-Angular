import dayjs from 'dayjs/esm';

import { ILeaveRequest, NewLeaveRequest } from './leave-request.model';

export const sampleWithRequiredData: ILeaveRequest = {
  id: 7430,
  title: 'tousle reschedule',
  fromDate: dayjs('2024-07-18'),
  toDate: dayjs('2024-07-19'),
  status: 'REQUESTED',
  department: 'MARKETING',
  changedAt: dayjs('2024-07-19T09:46'),
};

export const sampleWithPartialData: ILeaveRequest = {
  id: 12230,
  title: 'algorithm whether',
  description: 'nightlight',
  fromDate: dayjs('2024-07-19'),
  toDate: dayjs('2024-07-19'),
  status: 'REQUESTED',
  department: 'IT',
  changedAt: dayjs('2024-07-19T15:15'),
};

export const sampleWithFullData: ILeaveRequest = {
  id: 5916,
  title: 'liaise but questionably',
  description: 'calculator our helpful',
  fromDate: dayjs('2024-07-19'),
  toDate: dayjs('2024-07-19'),
  status: 'REQUESTED',
  department: 'RH',
  changedAt: dayjs('2024-07-18T22:47'),
};

export const sampleWithNewData: NewLeaveRequest = {
  title: 'surprisingly',
  fromDate: dayjs('2024-07-18'),
  toDate: dayjs('2024-07-19'),
  status: 'APPROVED',
  department: 'FINANCE',
  changedAt: dayjs('2024-07-18T23:04'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
