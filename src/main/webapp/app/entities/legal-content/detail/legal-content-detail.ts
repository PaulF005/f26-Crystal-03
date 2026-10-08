import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { ILegalContent } from '../legal-content.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-legal-content-detail',
  templateUrl: './legal-content-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class LegalContentDetail {
  readonly legalContent = input<ILegalContent | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
