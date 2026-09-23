import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { IQuestion } from '../question.model';

@Component({
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
