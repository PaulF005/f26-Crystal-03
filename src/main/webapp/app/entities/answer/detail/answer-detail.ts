import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IAnswer } from '../answer.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-answer-detail',
  templateUrl: './answer-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class AnswerDetail {
  readonly answer = input<IAnswer | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
