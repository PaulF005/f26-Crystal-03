import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IQuestion } from '../question.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-question-detail',
  templateUrl: './question-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class QuestionDetail {
  readonly question = input<IQuestion | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
