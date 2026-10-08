import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IConcept } from '../concept.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-concept-detail',
  templateUrl: './concept-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class ConceptDetail {
  readonly concept = input<IConcept | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
