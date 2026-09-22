import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { IStage } from '../stage.model';

@Component({
  selector: 'jhi-stage-detail',
  templateUrl: './stage-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class StageDetail {
  readonly stage = input<IStage | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
