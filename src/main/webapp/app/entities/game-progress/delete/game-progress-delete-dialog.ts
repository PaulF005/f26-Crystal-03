import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { IGameProgress } from '../game-progress.model';
import { GameProgressService } from '../service/game-progress.service';

@Component({
  templateUrl: './game-progress-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class GameProgressDeleteDialog {
  gameProgress?: IGameProgress;

  protected readonly gameProgressService = inject(GameProgressService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.gameProgressService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
