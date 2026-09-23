import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { IGame } from '../game.model';

@Component({
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
