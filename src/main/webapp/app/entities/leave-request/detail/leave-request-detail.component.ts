import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { ILeaveRequest } from '../leave-request.model';

@Component({
  standalone: true,
  selector: 'jhi-leave-request-detail',
  templateUrl: './leave-request-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class LeaveRequestDetailComponent {
  leaveRequest = input<ILeaveRequest | null>(null);

  previousState(): void {
    window.history.back();
  }
}
