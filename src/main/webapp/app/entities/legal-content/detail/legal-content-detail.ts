import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { ILegalContent } from '../legal-content.model';

@Component({
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
