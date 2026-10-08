import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IConceptProgress } from '../concept-progress.model';

@Component({
  selector: 'jhi-concept-progress-detail',
  templateUrl: './concept-progress-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class ConceptProgressDetail {
  readonly conceptProgress = input<IConceptProgress | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
