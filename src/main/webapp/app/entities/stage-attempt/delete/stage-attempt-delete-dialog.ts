import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { StageAttemptService } from '../service/stage-attempt.service';
import { IStageAttempt } from '../stage-attempt.model';

@Component({
  templateUrl: './stage-attempt-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class StageAttemptDeleteDialog {
  stageAttempt?: IStageAttempt;

  protected readonly stageAttemptService = inject(StageAttemptService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.stageAttemptService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
