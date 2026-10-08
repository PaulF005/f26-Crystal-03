import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ITopicProgress } from '../topic-progress.model';

@Component({
  selector: 'jhi-topic-progress-detail',
  templateUrl: './topic-progress-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class TopicProgressDetail {
  readonly topicProgress = input<ITopicProgress | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
