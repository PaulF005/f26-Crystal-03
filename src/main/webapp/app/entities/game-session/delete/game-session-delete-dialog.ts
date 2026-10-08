import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IGameSession } from '../game-session.model';
import { GameSessionService } from '../service/game-session.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './game-session-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class GameSessionDeleteDialog {
  gameSession?: IGameSession;

  protected readonly gameSessionService = inject(GameSessionService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.gameSessionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
