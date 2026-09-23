import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { IConcept } from '../concept.model';

@Component({
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
