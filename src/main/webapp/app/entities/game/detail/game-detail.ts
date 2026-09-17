import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IGame } from '../game.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-game-detail',
  templateUrl: './game-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class GameDetail {
  readonly game = input<IGame | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
