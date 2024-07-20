import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ILeaveRequest } from '../leave-request.model';
import { LeaveRequestService } from '../service/leave-request.service';

@Component({
  standalone: true,
  templateUrl: './leave-request-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class LeaveRequestDeleteDialogComponent {
  leaveRequest?: ILeaveRequest;

  protected leaveRequestService = inject(LeaveRequestService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.leaveRequestService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
