import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { ISource } from '../source.model';

@Component({
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
