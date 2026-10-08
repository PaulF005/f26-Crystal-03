import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IGame } from '../game.model';
import { GameService } from '../service/game.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './game-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class GameDeleteDialog {
  game?: IGame;

  protected readonly gameService = inject(GameService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.gameService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
