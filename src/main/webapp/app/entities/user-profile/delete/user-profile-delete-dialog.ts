import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { UserProfileService } from '../service/user-profile.service';
import { IUserProfile } from '../user-profile.model';

@Component({
  templateUrl: './user-profile-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class UserProfileDeleteDialog {
  userProfile?: IUserProfile;

  protected readonly userProfileService = inject(UserProfileService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userProfileService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
