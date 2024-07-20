import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { LeaveStatus } from 'app/entities/enumerations/leave-status.model';
import { Department } from 'app/entities/enumerations/department.model';

export interface ILeaveRequest {
  id: number;
  title?: string | null;
  description?: string | null;
  fromDate?: dayjs.Dayjs | null;
  toDate?: dayjs.Dayjs | null;
  status?: keyof typeof LeaveStatus | null;
  department?: keyof typeof Department | null;
  changedAt?: dayjs.Dayjs | null;
  employee?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewLeaveRequest = Omit<ILeaveRequest, 'id'> & { id: null };
