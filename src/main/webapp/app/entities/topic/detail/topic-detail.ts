import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { ITopic } from '../topic.model';

@Component({
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
