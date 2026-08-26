import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IProgress } from '../progress.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-progress-detail',
  templateUrl: './progress-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class ProgressDetail {
  readonly progress = input<IProgress | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
