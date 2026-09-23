import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IGameProgress } from '../game-progress.model';

@Component({
  selector: 'jhi-game-progress-detail',
  templateUrl: './game-progress-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class GameProgressDetail {
  readonly gameProgress = input<IGameProgress | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
