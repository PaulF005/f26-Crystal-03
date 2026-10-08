import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IGameSession } from '../game-session.model';

@Component({
  selector: 'jhi-game-session-detail',
  templateUrl: './game-session-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class GameSessionDetail {
  readonly gameSession = input<IGameSession | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
