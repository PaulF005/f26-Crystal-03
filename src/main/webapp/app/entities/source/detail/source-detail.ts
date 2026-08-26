import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { ISource } from '../source.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-source-detail',
  templateUrl: './source-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class SourceDetail {
  readonly source = input<ISource | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
