import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { StageService } from '../service/stage.service';
import { IStage } from '../stage.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './stage-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class StageDeleteDialog {
  stage?: IStage;

  protected readonly stageService = inject(StageService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.stageService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
