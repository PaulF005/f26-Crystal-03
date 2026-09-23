import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { IFeedback } from '../feedback.model';

@Component({
  selector: 'jhi-feedback-detail',
  templateUrl: './feedback-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class FeedbackDetail {
  readonly feedback = input<IFeedback | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
