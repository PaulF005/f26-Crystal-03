import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IFeedback } from '../feedback.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
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
