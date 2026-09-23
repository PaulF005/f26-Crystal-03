import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IStageAttempt } from '../stage-attempt.model';

@Component({
  selector: 'jhi-stage-attempt-detail',
  templateUrl: './stage-attempt-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class StageAttemptDetail {
  readonly stageAttempt = input<IStageAttempt | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
