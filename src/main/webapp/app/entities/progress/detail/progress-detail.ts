import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { IProgress } from '../progress.model';

@Component({
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
