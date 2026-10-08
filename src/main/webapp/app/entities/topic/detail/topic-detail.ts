import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { ITopic } from '../topic.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-topic-detail',
  templateUrl: './topic-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class TopicDetail {
  readonly topic = input<ITopic | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
